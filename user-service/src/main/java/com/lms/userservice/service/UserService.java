package com.lms.userservice.service;

import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.lms.userservice.database.UserDatabaseConnector;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-related operations.
 * This class provides methods for registering new users, retrieving all users,
 * and fetching users by their ID
 * All methods in this class delegate database operations to the {@link UserRepository}.
 *
 * @author Olan Healy
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserDatabaseConnector db;
    /**
     * Constructor for injecting the {@link UserRepository}.
     *
     * @param userRepository the repository used to manage users.
     * @param db
     */
    @Autowired
    public UserService(UserRepository userRepository, UserDatabaseConnector db) {

        this.userRepository = userRepository;
        this.db = db;
    }

    /**
     * Registers a new user by saving it to the database.
     * Also Checks for if the username already exists in database, or if email exists in database
     *
     * @param user the user entity to be registered.
     * @return the saved user entity.
     */
    public User registerUser(User user) {
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        return userRepository.save(user);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users.
     */

    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Validate the password ( no hashing at moment google auth later)
            if (user.getPasswordHash().equals(password)) {

                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Fetches a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the user if found, or null if not found.
     */

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Checks if an admin status is set to 'true'
     *
     * @param userId
     * @return true if status admin, otherwise false
     */
    public boolean isUserAdmin(String userId) {
        User user = getUserById(userId);
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }

    public boolean deleteUserById(String id) {
        // Check if user exists before deleting
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    // Update a user's name and/or email by ID
    public User updateUserById(String id, String newName, String newEmail) {
        // Find the user
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        // Update fields
        if (newName != null && !newName.isBlank()) user.setUsername(newName);
        if (newEmail != null && !newEmail.isBlank()) user.setEmail(newEmail);

        // Save updated user
        return userRepository.save(user);
    }

    public User updateUserBalance(String id, double amount) {

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        user.setBalance(user.getBalance() + amount);

        return userRepository.save(user);
    }
}
