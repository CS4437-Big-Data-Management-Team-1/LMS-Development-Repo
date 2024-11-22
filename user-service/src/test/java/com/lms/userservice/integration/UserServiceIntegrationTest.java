package com.lms.userservice.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.lms.userservice.database.UserDatabaseConnector;
import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private FirebaseAuth firebaseAuth;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();

        // Set database connection properties for our mock
        System.setProperty("DB_USERNAME", "sa");
        System.setProperty("DB_PASSWORD", "password");
        System.setProperty("DB_USERS_URL", "jdbc:h2:mem:testdb");

        // connect to db
        UserDatabaseConnector.connectToDB();

        // Mock FirebaseAuth behavior
        mockFirebaseAuth();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Mock the response from FirebaseAuth.createUser
        UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
        when(mockUserRecord.getUid()).thenReturn("test_uid");
        when(mockUserRecord.getEmail()).thenReturn("test_user@example.com");
        when(mockUserRecord.getDisplayName()).thenReturn("test_user");

        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                .thenReturn(mockUserRecord);

        // JSON payload for the request
        String userJson = """
            {
                "username": "test_user",
                "email": "test_user@example.com",
                "password": "Valid123$"
            }
        """;

        // Perform POST request and verify results
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test_user@example.com"))
                .andExpect(jsonPath("$.username").value("test_user"));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        // Insert a user to db before post request
        User user = createUser("test_id", "existing_user", "test_user@example.com");
        userRepository.save(user);

        String userJson = """
            {
                "username": "test_user",
                "email": "test_user@example.com",
                "password": "Valid123$"
            }
        """;

        // Attempt to register the same email and expect a 400 Bad Request
        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The user with the provided email already exists (EMAIL_EXISTS)."));
    }


    //===================
    //    HELPER METHODS
    //===================

    private User createUser(String id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hashed_password");
        user.setIsAdmin(false);
        return user;
    }

    private void mockFirebaseAuth() {
        try {
            // Mock the FirebaseAuth.createUser response
            UserRecord mockUserRecord = Mockito.mock(UserRecord.class);
            when(mockUserRecord.getUid()).thenReturn("mock_uid");
            when(mockUserRecord.getEmail()).thenReturn("mock_user@example.com");

            when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                    .thenReturn(mockUserRecord);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Mock FirebaseAuth setup failed", e);
        }
    }
}