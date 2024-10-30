package com.lms.userservice.controller;

import com.lms.userservice.login.UserLoginDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Unit tests for the UserController class
 *
 * @author Olan Healy
 */
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testUser");
        registrationDTO.setEmail("testuser@example.com");
        registrationDTO.setPassword("Valid123$");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testUser");
        savedUser.setEmail("testuser@example.com");

        doNothing().when(userValidator).validate(any(UserRegistrationDTO.class));
        when(userService.registerUser(any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = userController.registerUser(registrationDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
    }

    @Test
    void testRegisterUserInvalidUserName() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("test");

        doThrow(new IllegalArgumentException("Username must be longer than 4 characters"))
                .when(userValidator).validate(registrationDTO);

        ResponseEntity<?> response = userController.registerUser(registrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username must be longer than 4 characters", response.getBody());
    }

    @Test
    void testLoginUserSuccess() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("Valid123$");

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userService.loginUser(loginDTO.getEmail(), loginDTO.getPassword())).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.loginUser(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testLoginUserInvalidCredentials() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("testuser@example.com");
        loginDTO.setPassword("invalid123");

        when(userService.loginUser(loginDTO.getEmail(), loginDTO.getPassword())).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.loginUser(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUser1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUser2");
        userList.add(user1);
        userList.add(user2);

        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
    }

    @Test
    void testGetUserByIdUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByIdUserNotFound() {
        when(userService.getUserById(1L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
