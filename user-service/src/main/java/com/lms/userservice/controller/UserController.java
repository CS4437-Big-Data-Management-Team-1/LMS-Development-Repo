package com.lms.userservice.controller;

import com.lms.userservice.database.UserDatabaseConnector;
import com.lms.userservice.login.UserLoginDTO;
import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;;
import com.lms.userservice.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * REST Controller for managing user operations.
 * Handles user registration, fetching all users, and fetching user details by ID.
 * Handles user login,
 * @author olanhealy
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    UserDatabaseConnector db = new UserDatabaseConnector();

    //Log4j
    private static final Logger logger = LogManager.getLogger(UserController.class);
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
        db.connectToDB();
        this.userService = userService;
        this.userValidator = userValidator;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO Data Transfer Object containing the user's registration information (basic for now)
     * @return the saved User entity in the response body or error message if invalid registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        logger.info("Registering new user with email: {}", userDTO.getEmail());
        try {
            userValidator.validate(userDTO); // Validate user input
            logger.debug("User registration details validated successfully.");


            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPasswordHash(userDTO.getPassword());

            User savedUser = userService.registerUser(user); // Register the user
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /**
     * Handles request for a user logging in.
     *
     * accepts email and password via a POST request.
     * If the user is found and the password matches, the user details are returned with a 200 OK status. TODO login to app when up
     * If the credentials are invalid, a 401 Unauthorized status is returned.
     *
     * @param loginDTO (contains user details)
     * @return A Response Entity containing the User object if login is successful (placeholder)
     *         401 Unauthorized status if login fails.
     */

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserLoginDTO loginDTO) {
        logger.info("User login attempt with email: {}", loginDTO.getEmail());
        Optional<User> userOptional = userService.loginUser(loginDTO.getEmail(), loginDTO.getPassword());

        if (userOptional.isPresent()) {
            logger.info("User login successful for email: {}", loginDTO.getEmail());
            return ResponseEntity.ok(userOptional.get());
        } else {
            logger.warn("User login failed for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Fetches all registered users from the database.
     *
     * @return a list of all users in the response body
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users.");
        List<User> users = userService.getAllUsers();
        logger.debug("Number of users fetched: {}", users.size());
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
        logger.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        if (user != null) {
            logger.info("User found with ID: {}", id);
            return ResponseEntity.ok(user);
        } else {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}