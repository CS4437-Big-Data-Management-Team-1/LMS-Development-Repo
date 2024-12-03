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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import io.github.cdimascio.dotenv.Dotenv;

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

    @BeforeAll
    static void validateEnvironment() {
        Dotenv dotenv = Dotenv.load();
    }

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
        String adminToken = createAdminAndGetToken("admin_user@example.com", "AdminPassword123!", "Admin User");
        createTestUser("test_user@example.com", "ValidPassword123!", "Test User");

        mockMvc.perform(get("/api/users")
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin_user@example.com"))
                .andExpect(jsonPath("$[1].email").value("test_user@example.com"));
    }

    @Test
    @Order(14)
    void testGetUsersWithoutAuthorisationHeader() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Authorisation header.")));
    }

    @Test
    @Order(15)
    void testGetUsersWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorisation", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
    }
    @Test
    @Order(16)
    void testGetUsersAsNonAdmin() throws Exception {
        String userToken = createTestUserAndGetToken("regular_user@example.com", "ValidPassword123!", "Regular User");

        mockMvc.perform(get("/api/users")
                        .header("Authorisation", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Access denied: User is not an admin.")));
    }

    @Test
    @Order(17)
    void testGetUsersWithMultipleUsers() throws Exception {
        String adminToken = createAdminAndGetToken("multi_admin_user@example.com", "AdminPassword123!", "MultiAdmin");

        createTestUser("test_user1@example.com", "ValidPassword123!", "Test User 1");
        createTestUser("test_user2@example.com", "ValidPassword123!", "Test User 2");
        createTestUser("test_user3@example.com", "ValidPassword123!", "Test User 3");

        mockMvc.perform(get("/api/users")
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].email").value("multi_admin_user@example.com"))
                .andExpect(jsonPath("$[1].email").value("test_user1@example.com"))
                .andExpect(jsonPath("$[2].email").value("test_user2@example.com"))
                .andExpect(jsonPath("$[3].email").value("test_user3@example.com"));
    }

    //==============
    // GET USER BY ID
    //==============
    @Test
    @Order(18)
    void testGetUserByIdAsAdmin() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(get("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                {
                    "id": "%s",
                    "email": "testuser@example.com",
                    "username": "Test User"
                }
            """.formatted(testUser.getId())));
    }

    @Test
    @Order(19)
    void testGetUserByIdWithoutAuthorisationHeader() throws Exception {
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Authorisation header.")));
    }

    @Test
    @Order(20)
    void testGetUserByIdWithInvalidToken() throws Exception {
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(get("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
    }

    @Test
    @Order(21)
    void testGetUserByIdAsNonAdmin() throws Exception {
        String userToken = createTestUserAndGetToken("regularuser@example.com", "UserPass123!", "Regular User");
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(get("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Access denied: User is not an admin.")));
    }

    @Test
    @Order(22)
    void testGetNonExistentUserById() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");

        mockMvc.perform(get("/api/users/nonexistent_user_id")
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found.")));
    }

    //==============
    // DELETE USER BY ID
    //==============

    @Test
    @Order(23)
    void testDeleteExistingUserAsAdmin() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(delete("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
    }

    @Test
    @Order(24)
    void testDeleteNonexistentUser() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");

        mockMvc.perform(delete("/api/users/nonexistent_id")
                        .header("Authorisation", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }

    @Test
    @Order(25)
    void testDeleteUserWithoutAuthorisationHeader() throws Exception {
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Authorisation header."));
    }

    @Test
    @Order(26)
    void testDeleteUserWithInvalidToken() throws Exception {
        User testUser = createTestUser("testuser@example.com", "UserPass123!", "Test User");

        mockMvc.perform(delete("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorised: Invalid or expired token."));
    }

    //==============
    // UPDATE USER
    //==============

    @Test
    @Order(27)
    void testUpdateUserSuccessfully() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");
        User testUser = createTestUser("testuser@example.com", "TestPass123!", "Test User");

        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated User");
        updates.put("email", "updateduser@example.com");

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updateduser@example.com"));
    }

    @Test
    @Order(28)
    void testUpdateUserNotFound() throws Exception {
        String adminToken = createAdminAndGetToken("admin@example.com", "AdminPass123!", "Admin User");

        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated User");
        updates.put("email", "updateduser@example.com");

        mockMvc.perform(put("/api/users/nonexistent_id")
                        .header("Authorisation", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }

    @Test
    @Order(29)
    void testUpdateUserWithInvalidToken() throws Exception {
        User testUser = createTestUser("testuser@example.com", "TestPass123!", "Test User");

        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated User");
        updates.put("email", "updateduser@example.com");

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer invalid_token")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
    }

    @Test
    @Order(30)
    void testUpdateUserAsNonAdmin() throws Exception {
        String userToken = createTestUserAndGetToken("user@example.com", "UserPass123!", "Regular User");
        User testUser = createTestUser("testuser@example.com", "TestPass123!", "Test User");

        Map<String, String> updates = new HashMap<>();
        updates.put("name", "Updated User");
        updates.put("email", "updateduser@example.com");

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .header("Authorisation", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied: User is not an admin."));
    }

    @Test
    @Order(31)
    void testValidateJwtWithValidToken() throws Exception {
        String validToken = createTestUserAndGetToken("valid_user@example.com", "ValidPassword123!", "Valid User");

        mockMvc.perform(post("/api/users/validate-jwt")
                        .header("Authorisation", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Access granted for user:")));
    }

    @Test
    @Order(32)
    void testValidateJwtWithInvalidToken() throws Exception {
        String invalidToken = "invalid_token";

        mockMvc.perform(post("/api/users/validate-jwt")
                        .header("Authorisation", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
    }

    @Test
    @Order(33)
    void testValidateJwtWithoutToken() throws Exception {
        mockMvc.perform(post("/api/users/validate-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
    }

    @Test
    @Order(34)
    void testValidateJwtWithMalformedToken() throws Exception {
        String malformedToken = "BearerMalformedToken";

        mockMvc.perform(post("/api/users/validate-jwt")
                        .header("Authorisation", malformedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Unauthorised: Invalid or expired token")));
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
    private String createAdminAndGetToken(String email, String password, String displayName) throws Exception {
        User admin = createAdminUser(email, password, displayName);
        return loginAndGetToken(email, password);
    }

    private String createTestUserAndGetToken(String email, String password, String displayName) throws Exception {
        User testUser = createTestUser(email, password, displayName);
        return loginAndGetToken(email, password);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String loginJson = """
        {
            "email": "%s",
            "password": "%s"
        }
    """.formatted(email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String tokenResponse = loginResult.getResponse().getContentAsString();
        return tokenResponse.split(":")[1].trim();
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
