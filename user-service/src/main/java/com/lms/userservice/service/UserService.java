package com.lms.userservice.service;

import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /**
     * Constructor for injecting the {@link UserRepository}.
     *
     * @param userRepository the repository used to manage users.
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user by saving it to the database.
     * Also Checks for if the username already exists in database, or if email exists in database
     *
     * @param user the user entity to be registered.
     * @return the saved user entity.
     */
    public User registerUser(User user) {

        //check if email already in use in database
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        //check if username already in use in database
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
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
