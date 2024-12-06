package com.lms.userservice.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // Log4j
    private static final Logger logger = LogManager.getLogger(UserController.class);

    // Yse necessary classes
    private final UserService userService;
    private final UserValidator userValidator;
    private final RestTemplate restTemplate;
    private final UserDatabaseConnector db;

    // Used for login method
    @Value("${FIREBASE_API_KEY}")
    private String apiKey;

    /**
     * Constructs a UserController with injected dependencies for user service and validation.
     *
     * @param userService   Service to handle business logic for user operations
     * @param userValidator Validator to validate user input during registration
     */
    @Autowired
    public UserController(UserDatabaseConnector db, UserService userService, UserValidator userValidator, RestTemplate restTemplate) {
        this.db = db;
        this.userService = userService;
        this.userValidator = userValidator;
        this.restTemplate = restTemplate;
        logger.info("UserController initialised.");
    }

    // Construct the API URL dynamically
    private String getApiUrl() {
        return "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO Data Transfer Object containing the user's registration information (basic for now)
     * @return the saved User entity in the response body or error message if invalid registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userDTO)  throws Exception{
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

            // Fetch the ID token (similar to login)
            Map<String, String> body = new HashMap<>();
            body.put("email", userDTO.getEmail());
            body.put("password", userDTO.getPassword());
            body.put("returnSecureToken", "true");

            logger.debug("Sending request to Firebase login endpoint to retrieve idToken for registration.");

            ResponseEntity<Map> response = restTemplate.postForEntity(getApiUrl(), body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String idToken = (String) responseBody.get("idToken");

                logger.debug("Received ID token: {}", idToken);

                // Send the account creation notification with the idToken
                sendNotification(user.getEmail(), "account_creation", idToken);

                return ResponseEntity.ok("User successfully registered. Access the login endpoint.");
            } else {
                logger.warn("Login for ID token failed for user: {}", userDTO.getEmail());
                return ResponseEntity.status(500).body("Failed to retrieve ID token for user registration.");
            }

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

        if (loginDTO.getEmail() == null || loginDTO.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body("Email cannot be empty.");
        }
        if (!userValidator.isValidEmail(loginDTO.getEmail())) {
            return ResponseEntity.status(400).body("Invalid email format.");
        }

        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            return ResponseEntity.status(400).body("Password cannot be empty.");
        }

        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", loginDTO.getEmail());
            body.put("password", loginDTO.getPassword());
            body.put("returnSecureToken", "true");

            ResponseEntity<Map> response = restTemplate.postForEntity(getApiUrl(), body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String idToken = (String) responseBody.get("idToken");

                logger.info("Login successful for user: {}", loginDTO.getEmail());
                return ResponseEntity.ok("Token: " + idToken);
            } else {
                return ResponseEntity.status(401).body("Invalid credentials.");
            }
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
    }

    /**
     * Fetches all registered users from the database.
     *
     * This endpoint is restricted to admin users only. The caller must provide
     * a valid Firebase ID token in the `Authorisation` header. The ID token is verified,
     * and the user is checked for admin privileges before returning the list of all users.
     *
     * @param authorisationHeader The `Authorisation` header containing the Bearer token.
     * @return A list of all users if the requester is an admin, or an appropriate error response:
     *         - 400 if the `Authorisation` header is invalid.
     *         - 401 if the token is invalid or expired.
     *         - 403 if the requester is not an admin.
     *         - 500 if an internal server error occurs.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "Authorisation", required = false) String authorisationHeader) {
        try {
            if (authorisationHeader == null || authorisationHeader.isEmpty()) {
                throw new IllegalArgumentException("Invalid Authorisation header.");
            }

            validateToken(authorisationHeader, true);

            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            if (e.getMessage().contains("Access denied")) {
                logger.error("Authorisation error: {}", e.getMessage());
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                logger.error("Authentication error: {}", e.getMessage());
                return ResponseEntity.status(401).body(e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }


    /**
     * Fetches a specific user by their unique ID.
     *
     * This endpoint is restricted to admin users only. The caller must provide
     * a valid Firebase ID token in the `Authorisation` header. The ID token is verified,
     * and the user is checked for admin privileges before returning the requested user's details.
     *
     * @param id The unique ID of the user to retrieve.
     * @param authorisationHeader The `Authorisation` header containing the Bearer token.
     * @return The requested user's details if found and the requester is an admin, or an appropriate error response:
     *         - 400 if the `Authorisation` header is invalid.
     *         - 401 if the token is invalid or expired.
     *         - 403 if the requester is not an admin.
     *         - 404 if the user with the specified ID is not found.
     *         - 500 if an internal server error occurs.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable String id,
            @RequestHeader(value = "Authorisation", required = false) String authorisationHeader) {
        try {
            if (authorisationHeader == null || authorisationHeader.isEmpty()) {
                throw new IllegalArgumentException("Invalid Authorisation header.");
            }

            validateToken(authorisationHeader, true);

            User user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            if (e.getMessage().contains("Access denied")) {
                logger.error("Authorisation error: {}", e.getMessage());
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                logger.error("Authentication error: {}", e.getMessage());
                return ResponseEntity.status(401).body(e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error fetching user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    /**
     * Fetches the email of a user by their unique ID.
     * @param id
     * @return
     */
    @GetMapping("/{id}/email")
    public ResponseEntity<String> getUserEmailById(@PathVariable String id) {
        logger.info("Fetching email for user with ID: {}", id);
        User user = userService.getUserById(id);
        if (user != null) {
            logger.info("User found with ID: {}", id);
            return ResponseEntity.ok(user.getEmail());
        } else {
            logger.warn("User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * Restricted to admins. The caller must provide a valid Firebase ID token in the `Authorisation` header.
     *
     * @param id        The unique ID of the user to delete.
     * @param authorisationHeader The `Authorisation` header containing the Bearer token.
     * @return A success message if the user is deleted, or an appropriate error response:
     *         - 400 if the `Authorisation` header is invalid.
     *         - 401 if the token is invalid or expired.
     *         - 403 if the requester is not an admin.
     *         - 404 if the user does not exist.
     *         - 500 if an internal server error occurs.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(
            @PathVariable String id,
            @RequestHeader(value = "Authorisation", required = false) String authorisationHeader) {
        try {
            if (authorisationHeader == null || authorisationHeader.isEmpty()) {
                throw new IllegalArgumentException("Invalid Authorisation header.");
            }
            validateToken(authorisationHeader, true);
            boolean deleted = userService.deleteUserById(id);
            if (deleted) {
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            if (e.getMessage().contains("Access denied")) {
                logger.error("Authorisation error: {}", e.getMessage());
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                logger.error("Authentication error: {}", e.getMessage());
                return ResponseEntity.status(401).body(e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Updates a user's details by their ID.
     *
     * Restricted to admins. Allows updating the user's `name` or `email`.
     *
     * @param id        The unique ID of the user to update.
     * @param authorisationHeader The `Authorisation` header containing the Bearer token.
     * @param updates    A map containing the fields to update (`name` and/or `email`).
     * @return The updated user entity, or an appropriate error response:
     *         - 400 if the `Authorisation` header or update data is invalid.
     *         - 401 if the token is invalid or expired.
     *         - 403 if the requester is not an admin.
     *         - 404 if the user does not exist.
     *         - 500 if an internal server error occurs.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable String id,
            @RequestHeader("Authorisation") String authorisationHeader,
            @RequestBody Map<String, String> updates) {
        try {
            validateToken(authorisationHeader, true);

            if (updates == null || updates.isEmpty() ||
                    (updates.get("name") == null && updates.get("email") == null)) {
                return ResponseEntity.badRequest().body("Invalid update data provided.");
            }

            String newName = updates.get("name");
            String newEmail = updates.get("email");

            User updatedUser = userService.updateUserById(id, newName, newEmail);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (SecurityException e) {
            if (e.getMessage().contains("Unauthorised")) {
                return ResponseEntity.status(401).body(e.getMessage());
            }
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    /**
     * PLACEHOLDER: Just for testing JWT token
     * @param authorisationHeader
     * @return
     */
    @PostMapping("/validate-jwt")
    public ResponseEntity<?> secureEndpoint(@RequestHeader(value = "Authorisation", required = false) String authorisationHeader) {
        logger.info("Accessing secure endpoint.");
        try {
            if (authorisationHeader == null || authorisationHeader.isEmpty()) {
                throw new IllegalArgumentException("Missing Authorisation header.");
            }

            String idToken = authorisationHeader.replace("Bearer ", "");
            logger.debug("Verifying ID token: {}", idToken);

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            logger.info("Token verified. Access granted for UID: {}", uid);

            return ResponseEntity.ok("Access granted for user: " + uid);
        } catch (IllegalArgumentException e) {
            logger.error("Unauthorised access due to missing or empty header: {}", e.getMessage());
            return ResponseEntity.status(401).body("Unauthorised: Invalid or expired token");
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
    private void sendNotification(String recipient, String type, String idToken) {
        String notificationUrl = "http://notification-service:8085/api/notifications/send";
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("recipient", recipient);
        notificationData.put("type", type);
        notificationData.put("idToken", idToken);

        try {
            restTemplate.postForEntity(notificationUrl, notificationData, String.class);
            logger.info("Notification request sent for type: {}", type);
        } catch (Exception e) {
            logger.error("Failed to send notification request for type {}: {}", type, e.getMessage());
        }
    }

    /**
     * Validates a Firebase token and optionally checks if the user has admin privileges.
     *
     * @param authorisationHeader The "Authorisation" header containing the Bearer token.
     * @param requireAdmin        Flag indicating if admin privileges are required.
     * @return The Firebase user ID if validation is successful.
     * @throws FirebaseAuthException if the token is invalid or user does not meet admin requirements.
     */
    protected String validateToken(String authorisationHeader, boolean requireAdmin) {
        if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorisation header.");
        }

        String idToken = authorisationHeader.replace("Bearer ", "").trim();

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();

            if (requireAdmin && !userService.isUserAdmin(userId)) {
                throw new SecurityException("Access denied: User is not an admin.");
            }

            return userId;
        } catch (FirebaseAuthException e) {
            logger.error("Failed to validate Firebase token: {}", e.getMessage(), e);
            throw new SecurityException("Unauthorised: Invalid or expired token.");
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage(), e);
            throw new SecurityException("Unauthorised: Unable to validate token.");
        }
    }

}
