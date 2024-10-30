package com.lms.userservice.registration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserRegistrationDTO class
 * @author Olan Healu
 */
public class UserRegistrationDTOTest {

    @Test
    public void testUsernameGetterAndSetter() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("testUser");

        assertEquals("testUser", userDTO.getUsername(), "Username should be 'testUser'");
    }

    @Test
    public void testEmailGetterAndSetter() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setEmail("test@example.com");

        assertEquals("test@example.com", userDTO.getEmail(), "Email should be 'test@example.com'");
    }

    @Test
    public void testPasswordGetterAndSetter() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setPassword("securePassword");

        assertEquals("securePassword", userDTO.getPassword(), "Password should be 'securePassword'");
    }
}