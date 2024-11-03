package com.lms.userservice.controller;

import com.lms.userservice.login.UserLoginDTO;
import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;;
import com.lms.userservice.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.util.List;

/**
 * REST Controller for managing user operations.
 * Handles user registration, fetching all users, and fetching user details by ID.
 * Handles user login,
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
     * @return the saved User entity in the response body or error message if invalid registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        try {
            userValidator.validate(userDTO);
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(userDTO.getEmail())
                    .setPassword(userDTO.getPassword())
                    .setDisplayName(userDTO.getUsername());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);


            User user = new User();
            user.setEmail(userRecord.getEmail());
            user.setUsername(userRecord.getDisplayName());
            user.setPasswordHash(""); // TODO can probs get rid of this as firebase deal with password

            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException | FirebaseAuthException e) {
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
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO) {
        try {
            String idToken = FirebaseAuth.getInstance().getUserByEmail(loginDTO.getEmail()).getUid();
            return ResponseEntity.ok("Login successful. Token: " + idToken);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
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
