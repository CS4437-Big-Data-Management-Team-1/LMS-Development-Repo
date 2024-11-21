package com.lms.userservice.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    // Yse necessary classes
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

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, body, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String idToken = (String) responseBody.get("idToken");

                logger.debug("Received ID token: {}", idToken);
                
                // Send the account creation notification with the idToken
                sendNotification(user.getEmail(), "account_creation", idToken);

                return ResponseEntity.ok(savedUser);
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
                User user = db.searchForUser(uid);

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
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorisation") String authorisationHeader) {
        logger.info("Fetching all users.");

        try {
            // Validate and extract ID token from header
            if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorisation header.");
                return ResponseEntity.status(400).body("Invalid Authorisation header.");
            }

            String idToken = authorisationHeader.replace("Bearer ", "").trim();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();
            logger.debug("Verified token for user ID: {}", userId);

            // Check if user has admin privileges
            if (!userService.isUserAdmin(userId)) {
                logger.warn("Access denied: User is not an admin.");
                return ResponseEntity.status(403).body("Access denied. Admin only.");
            }

            // Fetch all users if the user is an admin
            List<User> users = userService.getAllUsers();
            logger.debug("Number of users fetched: {}", users.size());
            return ResponseEntity.ok(users);

        } catch (FirebaseAuthException e) {
            logger.error("Error verifying ID token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An error occurred while fetching users.");
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
            @RequestHeader("Authorisation") String authorisationHeader) {
        logger.info("Fetching user with ID: {}", id);

        try {
            // Validate and extract ID token from header
            if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorisation header.");
                return ResponseEntity.status(400).body("Invalid Authorisation header.");
            }

            String idToken = authorisationHeader.replace("Bearer ", "").trim();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();
            logger.debug("Verified token for user ID: {}", userId);

            // Check admin privileges
            if (!userService.isUserAdmin(userId)) {
                logger.warn("Access denied: User is not an admin.");
                return ResponseEntity.status(403).body("Access denied. Admin only.");
            }

            // Fetch the user by ID
            User user = userService.getUserById(id);
            if (user != null) {
                logger.info("User found with ID: {}", id);
                return ResponseEntity.ok(user);
            } else {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.status(404).body("User not found.");
            }

        } catch (FirebaseAuthException e) {
            logger.error("Error verifying ID token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            logger.error("Error fetching user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An error occurred while fetching the user.");
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
            @RequestHeader("Authorisation") String authorisationHeader) {
        logger.info("Attempting to delete user with ID: {}", id);

        try {
            // Validate and extract ID token from header
            if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorisation header.");
                return ResponseEntity.status(400).body("Invalid Authorisation header.");
            }

            String idToken = authorisationHeader.replace("Bearer ", "").trim();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();
            logger.debug("Verified token for user ID: {}", userId);

            // Check admin privileges
            if (!userService.isUserAdmin(userId)) {
                logger.warn("Access denied: User is not an admin.");
                return ResponseEntity.status(403).body("Access denied. Admin only.");
            }

            // Delete the user
            boolean deleted = userService.deleteUserById(id);
            if (deleted) {
                logger.info("User with ID {} deleted successfully.", id);
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                logger.warn("User with ID {} not found.", id);
                return ResponseEntity.status(404).body("User not found.");
            }

        } catch (FirebaseAuthException e) {
            logger.error("Error verifying ID token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An error occurred while deleting the user.");
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
        logger.info("Attempting to update user with ID: {}", id);

        try {
            // Validate and extract ID token from header
            if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorisation header.");
                return ResponseEntity.status(400).body("Invalid Authorisation header.");
            }

            String idToken = authorisationHeader.replace("Bearer ", "").trim();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();
            logger.debug("Verified token for user ID: {}", userId);

            // Check admin privileges
            if (!userService.isUserAdmin(userId)) {
                logger.warn("Access denied: User is not an admin.");
                return ResponseEntity.status(403).body("Access denied. Admin only.");
            }

            // Validate and process update data
            String newName = updates.get("name");
            String newEmail = updates.get("email");
            if (newName == null && newEmail == null) {
                logger.warn("No valid update fields provided.");
                return ResponseEntity.status(400).body("No valid update fields provided.");
            }

            // Update the user
            User updatedUser = userService.updateUserById(id, newName, newEmail);
            if (updatedUser != null) {
                logger.info("User with ID {} updated successfully.", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.warn("User with ID {} not found.", id);
                return ResponseEntity.status(404).body("User not found.");
            }

        } catch (FirebaseAuthException e) {
            logger.error("Error verifying ID token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body("Invalid or expired token.");
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An error occurred while updating the user.");
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
    private void sendNotification(String recipient, String type, String idToken) {
        String notificationUrl = "http://localhost:8085/api/notifications/send";
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
}
