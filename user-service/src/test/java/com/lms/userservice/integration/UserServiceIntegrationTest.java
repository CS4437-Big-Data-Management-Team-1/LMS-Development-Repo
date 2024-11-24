package com.lms.userservice.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    // REGISTRATION
    //==============
    @Test
    @Order(1)
    void testRegisterUser_Success() throws Exception {
        createTestUser("test_user@example.com", "ValidPassword123!", "test_user");

        assertEquals(1, userRepository.count());

        User createdUser = userRepository.findByEmail("test_user@example.com")
                .orElseThrow(() -> new IllegalStateException("User not found in database after registration"));
        createdFirebaseUserIds.add(createdUser.getId());
    }

    @Test
    @Order(2)
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
    @Order(3)
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
    @Order(4)
    void testDuplicateEmail() throws Exception {
        createTestUser("duplicate@example.com", "ValidPassword123!", "existing_user");

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
    // LOGIN
    //==============
    @Test
    @Order(5)
    void testLoginUser_Success() throws Exception {
        createTestUser("login_user@example.com", "ValidPassword123!", "login_user");

        String loginJson = """
        {
            "email": "login_user@example.com",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Token:")));
    }

    @Test
    @Order(6)
    void testLoginWithInvalidEmailFormat() throws Exception {
        String loginJson = """
        {
            "email": "invalid_email_format",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));
    }

    @Test
    @Order(7)
    void testLoginWithEmptyPassword() throws Exception {
        String loginJson = """
        {
            "email": "user@example.com",
            "password": ""
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password cannot be empty")));
    }

    @Test
    @Order(8)
    void testLoginWithEmptyEmail() throws Exception {
        String loginJson = """
        {
            "email": "",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email cannot be empty")));
    }

    @Test
    @Order(9)
    void testLoginWithNonExistentUser() throws Exception {
        String loginJson = """
        {
            "email": "nonexistent@example.com",
            "password": "ValidPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials.")));
    }

    @Test
    @Order(10)
    void testLoginWithIncorrectPassword() throws Exception {
        createTestUser("incorrect_password@example.com", "ValidPassword123!", "incorrect_password_user");

        String loginJson = """
        {
            "email": "incorrect_password@example.com",
            "password": "WrongPassword123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials.")));
    }

    @Test
    @Order(11)
    void testLoginWithoutBody() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    void testLoginWithMalformedJson() throws Exception {
        String malformedJson = """
    {
        "email": "user@example.com"
        "password": "ValidPassword123!"
    """;

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    //==============
    // GET ALL USERS
    //==============
    @Test
    @Order(13)
    void testGetUsersAsAdmin() throws Exception {
        // Create admin user and log in
        User adminUser = createAdminUser("admin_user@example.com", "AdminPassword123!", "Admin User");

        String loginJson = """
        {
            "email": "admin_user@example.com",
            "password": "AdminPassword123!"
        }
        """;

        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = loginResult.getResponse().getContentAsString().split(":")[1].trim();

        // Create a test user
        createTestUser("test_user@example.com", "ValidPassword123!", "Test User");

        // Call the GET /api/users endpoint
        mockMvc.perform(get("/api/users")
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin_user@example.com"))
                .andExpect(jsonPath("$[1].email").value("test_user@example.com"));
    }

    //==============
    // Helper Methods
    //==============

    private User createAdminUser(String email, String password, String displayName) throws FirebaseAuthException {
        UserRecord.CreateRequest adminRequest = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(displayName);

        UserRecord adminRecord = FirebaseAuth.getInstance().createUser(adminRequest);
        createdFirebaseUserIds.add(adminRecord.getUid());

        User adminUser = new User();
        adminUser.setId(adminRecord.getUid());
        adminUser.setEmail(adminRecord.getEmail());
        adminUser.setUsername(adminRecord.getDisplayName());
        adminUser.setIsAdmin(true);
        adminUser.setPasswordHash("");
        userRepository.save(adminUser);

        return adminUser;
    }

    private User createTestUser(String email, String password, String displayName) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(displayName);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        createdFirebaseUserIds.add(userRecord.getUid());

        User testUser = new User();
        testUser.setId(userRecord.getUid());
        testUser.setEmail(userRecord.getEmail());
        testUser.setUsername(userRecord.getDisplayName());
        testUser.setIsAdmin(false);
        testUser.setPasswordHash("");
        userRepository.save(testUser);

        return testUser;
    }

    private void deleteUserFromFirebase(String userId) {
        if (userId == null || userId.isEmpty()) {
            System.err.println("Skipping deletion for null or empty userId.");
            return;
        }
        try {
            FirebaseAuth.getInstance().deleteUser(userId);
            System.out.println("Successfully deleted Firebase user with ID: " + userId);
        } catch (FirebaseAuthException e) {
            System.err.println("Failed to delete Firebase user with ID " + userId + ": " + e.getMessage());
        }
    }
}
