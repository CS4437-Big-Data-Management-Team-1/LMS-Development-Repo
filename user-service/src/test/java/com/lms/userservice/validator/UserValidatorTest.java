package com.lms.userservice.validator;

import com.lms.userservice.registration.UserRegistrationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the UserValidator class
 * @author Olan Healu
 */

class UserValidatorTest {

    private UserValidator userValidator;

    @BeforeEach
    public void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    public void testValidUserRegistrationDetails() {
        UserRegistrationDTO validUser = new UserRegistrationDTO();
        validUser.setUsername("TestUser");
        validUser.setEmail("testemail@example.com");
        validUser.setPassword("Valid123$");

        userValidator.validate(validUser);
    }

    @Test
    public void testEmptyUsernameThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("");
        userDTO.setEmail("testemail@example.com");
        userDTO.setPassword("Valid123$");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Username is required");
    }

    @Test
    public void testShortUsernameThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("Test");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("Valid123$");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Username must be longer than 4 characters");
    }

    @Test
    public void testEmptyEmailThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("");
        userDTO.setPassword("Valid123$");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Email is required");
    }

    @Test
    public void testInvalidEmailThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("TestUser@");
        userDTO.setPassword("Valid123$");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Invalid email format");
    }

    @Test
    public void testEmptyPasswordThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Password is required");
    }

    @Test
    public void testInvalidPasswordThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("weakpass");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Password must have at least 1 lowercase letter, 1 uppercase letter, 1 digit, 1 special character, and be at least 8 characters long");
    }

    @Test
    public void testPasswordWithoutSpecialCharacterThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("Valid123");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Password must have at least 1 lowercase letter, 1 uppercase letter, 1 digit, 1 special character, and be at least 8 characters long");
    }

    @Test
    public void testPasswordWithoutUppercaseCharacterThrowsException() {
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        userDTO.setUsername("TestUser");
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("valid123$");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDTO), "Password must have at least 1 lowercase letter, 1 uppercase letter, 1 digit, 1 special character, and be at least 8 characters long");
    }
}
