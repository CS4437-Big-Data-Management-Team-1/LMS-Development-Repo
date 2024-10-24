package com.lms.userservice.service;

import com.lms.userservice.model.User;
import com.lms.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lms.userservice.database.UserDatabaseConnector;
import java.util.List;

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
     *
     * @param user the user entity to be registered.
     * @return the saved user entity.
     */
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users.
     */
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

    public static void main(String[] args) {

        //just sample test to check if userservice can add values to DB
        UserDatabaseConnector database = new UserDatabaseConnector();
        database.connectToDB();
        database.addUserToDB();
        database.disconnectFromDB();
    }

}
