package com.lms.userservice.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for User Service
 * @author Olan Healy
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private List<String> createdFirebaseUserIds;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        createdFirebaseUserIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        for (String userId : createdFirebaseUserIds) {
            deleteUserFromFirebase(userId);
        }
    }

    //==============
    //REGISTRATION
    //==============
    @Test
    void testRegisterUser_Success() throws Exception {
        String userJson = """
        {
            "username": "test_user",
            "email": "test_user@example.com",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.email").value("test_user@example.com"));

        assertEquals(1, userRepository.count());


        User createdUser = userRepository.findAll().get(0);
        createdFirebaseUserIds.add(createdUser.getId());
    }

    @Test
    void testInvalidEmail() throws Exception {
        String userJson = """
        {
            "username": "invalid_email_user",
            "email": "invalid_email",
            "password": "StrongPass123!"
        }
        """;

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));
    }

    @Test
    void testWeakPassword() throws Exception {
        String userJson = """
        {
            "username": "weak_password_user",
            "email": "weak_password_user@example.com",
            "password": "123"
        }
        """;

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must have at least 1 lowercase letter, 1 uppercase letter, 1 digit, 1 special character, and be at least 8 characters long")));
    }

    @Test
    void testDuplicateEmail() throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("duplicate@example.com")
                .setPassword("ValidPassword123!")
                .setDisplayName("existing_user");

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        createdFirebaseUserIds.add(userRecord.getUid());


        String userJson = """
        {
            "username": "new_user",
            "email": "duplicate@example.com",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("The user with the provided email already exists (EMAIL_EXISTS).")));
    }

    //==============
    //HELPER METHODS
    //==============

    private void deleteUserFromFirebase(String userId) {
        if (userId == null || userId.isEmpty()) return;
        try {
            FirebaseAuth.getInstance().deleteUser(userId);
            System.out.println("Deleted Firebase user with ID: " + userId);
        } catch (FirebaseAuthException e) {
            System.err.println("Failed to delete Firebase user: " + e.getMessage());
        }
    }
}
