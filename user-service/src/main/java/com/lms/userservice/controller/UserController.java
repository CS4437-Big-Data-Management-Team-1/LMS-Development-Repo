package com.lms.userservice.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.lms.userservice.database.UserDatabaseConnector;
import com.lms.userservice.login.UserLoginDTO;
import com.lms.userservice.model.User;
import com.lms.userservice.registration.UserRegistrationDTO;
import com.lms.userservice.service.UserService;
import com.lms.userservice.validator.UserValidator;

/**
 * REST Controller for managing user operations.
 * Handles user registration, fetching all users, and fetching user details by ID.
 * Handles user login, with Firebase JWT Authorisation
 *
 * @see <a href="https://www.baeldung.com/spring-security-firebase-authentication"> Setting up Firebase authentication and authorisation</a>
 * @author olanhealy
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    UserDatabaseConnector db = new UserDatabaseConnector();

    // Log4j
    private static final Logger logger = LogManager.getLogger(UserController.class);

    // Yse necessacary classes
    private final UserService userService;
    private final UserValidator userValidator;
    private final RestTemplate restTemplate;

    // Used for login method
    private final String apiKey = System.getProperty("FIREBASE_API_KEY");
    private final String apiUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

    /**
     * Constructs a UserController with injected dependencies for user service and validation.
     *
     * @param userService   Service to handle business logic for user operations
     * @param userValidator Validator to validate user input during registration
     */
    @Autowired
    public UserController(UserService userService, UserValidator userValidator, RestTemplate restTemplate) {
        db.connectToDB();
        this.userService = userService;
        this.userValidator = userValidator;
        this.restTemplate = restTemplate;
        logger.info("UserController initialised.");
    }

    /**
     * Registers a new user.
     *
     * @param userDTO Data Transfer Object containing the user's registration information (basic for now)
     * @return the saved User entity in the response body or error message if invalid registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO) {
        logger.info("Attempting to register user with email: {}", userDTO.getEmail());
        try {
            userValidator.validate(userDTO);
            logger.debug("User data validated for email: {}", userDTO.getEmail());

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(userDTO.getEmail())
                    .setPassword(userDTO.getPassword())
                    .setDisplayName(userDTO.getUsername());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            logger.info("Firebase user created with UID: {}", userRecord.getUid());

            User user = new User();
            user.setId(userRecord.getUid());
            user.setEmail(userRecord.getEmail());
            user.setUsername(userRecord.getDisplayName());
            user.setPasswordHash(""); // TODO can probs get rid of this as firebase deal with password
            logger.debug("User entity prepared for saving.");

            User savedUser = userService.registerUser(user);
            logger.info("User registered and saved with ID: {}", savedUser.getId());

            sendNotification(user.getEmail(), "account_creation");

            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException | FirebaseAuthException e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Handles request for a user logging in.
     *
     * Authenticates the user using Firebase by sending a POST request with the user's email and password.
     * If the email and password are correct, Firebase returns a JWT ID token which will
     * be used for accessing specific endpoints, and the method responds with a 200 OK status.
     * If the authentication fails, a 401 Unauthorized status is returned.
     *
     * @param loginDTO contains the user's email and password for authentication
     * @return A Response Entity containing a success message and the ID token if login is successful,
     *         or a 401 Unauthorized status if the login fails.
     */

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO) {
        logger.info("Attempting login for user: {}", loginDTO.getEmail());
        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", loginDTO.getEmail());
            body.put("password", loginDTO.getPassword());
            body.put("returnSecureToken", "true");
            logger.debug("Sending request to Firebase login endpoint.");

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {

                logger.info("Login successful for user: {}", loginDTO.getEmail());
                Map<String, Object> responseBody = response.getBody();

                String idToken = (String) responseBody.get("idToken");
                String uid = (String) responseBody.get("localId");
                System.out.println(uid);
                User user = db.searchForUser(uid);

                System.out.println(user.getUsername());
                logger.debug("Received ID token: {}", idToken);
                return ResponseEntity.ok("Login successful. Token: " + idToken);
            } else {
                logger.warn("Login failed for user: {}", loginDTO.getEmail());
                return ResponseEntity.status(401).body("Invalid email or password");
            }
        } catch (Exception e) {
            logger.error("Error during login for user: {}", loginDTO.getEmail(), e);
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

    /**
     * PLACEHOLDER: Just for testing JWT token
     * @param authorisationHeader
     * @return
     */
    @PostMapping("/validate-jwt")
    public ResponseEntity<?> secureEndpoint(@RequestHeader("Authorisation") String authorisationHeader) {
        logger.info("Accessing secure endpoint.");
        try {
            String idToken = authorisationHeader.replace("Bearer ", "");
            System.out.println(idToken);
            logger.debug("Verifying ID token: {}", idToken);

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            logger.info("Token verified. Access granted for UID: {}", uid);

            return ResponseEntity.ok("Access granted for user: " + uid);
        } catch (Exception e) {
            logger.error("Unauthorised access attempt.", e);
            return ResponseEntity.status(401).body("Unauthorised: Invalid or expired token");
        }
    }
    /**
     * Sends a notification to the given email with the specified type.
     *
     * @param recipient The email address of the recipient
     * @param type      The type of notification (e.g., "account_creation")
     */
    private void sendNotification(String recipient, String type) {
        String notificationUrl = "http://localhost:8085/api/notifications/send";
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("recipient", recipient);
        notificationData.put("type", type);

        try {
            restTemplate.postForEntity(notificationUrl, notificationData, String.class);
            logger.info("Notification request sent for type: {}", type);
        } catch (Exception e) {
            logger.error("Failed to send notification request for type {}: {}", type, e.getMessage());
        }
    }
}
