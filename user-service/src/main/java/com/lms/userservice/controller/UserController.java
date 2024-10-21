package com.lms.userservice.controller;

import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;;
import com.lms.userservice.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user operations.
 * Handles user registration, fetching all users, and fetching user details by ID.
 * @author olanhealy
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Yse necessacary classes
    private final UserService userService;
    private final UserValidator userValidator;

    /**
     * Constructs a UserController with injected dependencies for user service and validation.
     *
     * @param userService   Service to handle business logic for user operations
     * @param userValidator Validator to validate user input during registration
     */
    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO Data Transfer Object containing the user's registration information (basic for now)
     * @return the saved User entity in the response body
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        userValidator.validate(userDTO);
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(userDTO.getPassword()); //TODO Password Hashing
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Fetches all registered users from the database.
     *
     * @return a list of all users in the response body
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Fetches a user by their unique ID or 404 if not found.
     *
     * @param id the unique ID of the user
     * @return the User entity or 404 Not Found if the user does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}
