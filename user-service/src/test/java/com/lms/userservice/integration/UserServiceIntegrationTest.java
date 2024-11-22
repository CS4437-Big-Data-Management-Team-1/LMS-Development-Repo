package com.lms.userservice.integration;

import com.lms.userservice.database.UserDatabaseConnector;
import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import com.lms.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        userRepository.deleteAll();

        // Set system properties for the UserDatabaseConnector
        System.setProperty("DB_USERNAME", "sa");
        System.setProperty("DB_PASSWORD", "password");
        System.setProperty("DB_USERS_URL", "jdbc:h2:mem:testdb");
        // Ensure the static connection is initialised
        UserDatabaseConnector.connectToDB();
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setId("test_id");
        user.setUsername("test_user");
        user.setEmail("test_user@example.com");
        user.setPasswordHash("hashed_password");
        user.setIsAdmin(false);

        User savedUser = userService.registerUser(user);
        assertNotNull(savedUser);

        Optional<User> fetchedUser = userRepository.findById(savedUser.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals(user.getEmail(), fetchedUser.get().getEmail());
    }
}
