package com.lms.userservice.controller;

import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;
import com.lms.userservice.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserController class
 *
 * @Author Olan Healy
 */
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    private UserRegistrationDTO userRegistrationDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setUsername("testUser");
        userRegistrationDTO.setEmail("testuser@example.com");
        userRegistrationDTO.setPassword("Valid123$");

        user = new User();
        user.setId("user-id");
        user.setEmail("testuser@example.com");
        user.setUsername("testUser");
    }

    @Test
    void testRegisterUserInvalidUserName() throws Exception {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("test"); // Invalid username (too short)

        doThrow(new IllegalArgumentException("Username must be longer than 4 characters"))
                .when(userValidator).validate(registrationDTO);

        ResponseEntity<?> response = userController.registerUser(registrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username must be longer than 4 characters", response.getBody());
    }

    @Test
    void testRegisterUserInvalidInput() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input"))
                .when(userValidator).validate(any(UserRegistrationDTO.class));

        ResponseEntity<?> response = userController.registerUser(userRegistrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody());
    }

    @Test
    void testGetAllUsersInvalidAuthHeader() {
        ResponseEntity<?> response = userController.getAllUsers(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Authorisation header.", response.getBody());
    }

    @Test
    void testGetUserByIdInvalidAuthHeader() {
        ResponseEntity<?> response = userController.getUserById("user-id", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Authorisation header.", response.getBody());
    }

    @Test
    void testDeleteUserByIdInvalidAuthHeader() {
        ResponseEntity<?> response = userController.deleteUserById("user-id", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Authorisation header.", response.getBody());
    }

    @Test
    void testUpdateUserByIdInvalidAuthHeader() {
        Map<String, String> updates = new HashMap<>();
        updates.put("name", "New Name");

        ResponseEntity<?> response = userController.updateUserById("user-id", null, updates);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Authorisation header.", response.getBody());
    }
}