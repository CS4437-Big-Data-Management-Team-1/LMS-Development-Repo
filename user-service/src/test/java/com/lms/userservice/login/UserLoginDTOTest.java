package com.lms.userservice.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the UserLoginDTO class
 * @author Olan Healy
 */
class UserLoginDTOTest {

    private UserLoginDTO userLoginDTO;

    @BeforeEach
    void setUp() {
        userLoginDTO = new UserLoginDTO();
    }

    @Test
    void testSetAndGetEmail() {
        userLoginDTO.setEmail("test@example.com");
        assertEquals("test@example.com", userLoginDTO.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        userLoginDTO.setPassword("testPassword123");
        assertEquals("testPassword123", userLoginDTO.getPassword());
    }
}
