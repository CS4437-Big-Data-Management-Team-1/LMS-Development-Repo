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
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing user operations.
 * Handles user registration, fetching all users, and fetching user details by ID.
 * Handles user login,
 *
 * @see <a href="https://www.baeldung.com/spring-security-firebase-authentication"> Setting up Firebase authentication and authorisation</a>
 * @author olanhealy
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Yse necessacary classes
    private final UserService userService;
    private final UserValidator userValidator;

    //Used for login method
    private final String apiKey = System.getProperty("FIREBASE_API_KEY");
    private final String apiUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

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
     * Authenticates the user using Firebase by sending a POST request with the user's email and password.
     * If the email and password are correct, Firebase returns an ID token, and the method responds with a 200 OK status.
     * If the authentication fails, a 401 Unauthorized status is returned.
     *
     * @param loginDTO contains the user's email and password for authentication
     * @return A Response Entity containing a success message and the ID token if login is successful,
     *         or a 401 Unauthorized status if the login fails.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", loginDTO.getEmail());
            body.put("password", loginDTO.getPassword());
            body.put("returnSecureToken", "true");


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String idToken = (String) responseBody.get("idToken");
                return ResponseEntity.ok("Login successful. Token: " + idToken);
            } else {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
        } catch (Exception e) {
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
