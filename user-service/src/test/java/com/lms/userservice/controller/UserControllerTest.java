package com.lms.userservice.controller;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.lms.userservice.login.UserLoginDTO;
import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;
import com.lms.userservice.validator.UserValidator;

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

    @Mock
    private User mockUser;

    private MockMvc mockMvc;

    private UserRegistrationDTO userRegistrationDTO;
    private UserLoginDTO userLoginDTO;
    private User user;

    private static final String MOCK_USER_ID = "test-uid-123";
    private static final String MOCK_USER_EMAIL = "testuser@gmail.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setUsername("testUser");
        userRegistrationDTO.setEmail("testuser@example.com");
        userRegistrationDTO.setPassword("Valid123$");

        userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("testuser@example.com");
        userLoginDTO.setPassword("Valid123$");

        user = new User();
        user.setId("abcdefg");
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
    void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
    }

    @Test
    void testGetUserByIdUserExists() {
        String userId = "test-uid-123";
        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByIdUserNotFound() {
        String userId = "non-existing-uid";
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserEmailById_UserFound() throws Exception {

        when(userService.getUserById(MOCK_USER_ID)).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn(MOCK_USER_EMAIL);

        mockMvc.perform(get("/api/users/{id}/email", MOCK_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(MOCK_USER_EMAIL));
        verify(userService, times(1)).getUserById(MOCK_USER_ID);
        verify(mockUser, times(1)).getEmail();
    }

    @Test
    void testGetUserEmailById_UserNotFound() throws Exception {
        when(userService.getUserById(MOCK_USER_ID)).thenReturn(null);
        mockMvc.perform(get("/api/users/{id}/email", MOCK_USER_ID))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).getUserById(MOCK_USER_ID);
    }

}
