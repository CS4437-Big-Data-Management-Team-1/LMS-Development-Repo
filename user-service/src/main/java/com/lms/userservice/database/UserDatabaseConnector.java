package com.lms.userservice.database;

import com.lms.userservice.model.User;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Component
public class UserDatabaseConnector {

    private static final Logger log;

    private final DataSource dataSource;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(UserDatabaseConnector.class.getName());
    }

    @Autowired
    public UserDatabaseConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Connects to the database to test the connection.
     *
     * @return True if the connection test is successful, false otherwise.
     */
    public boolean connectToDB() {
        log.info("Connecting to the database");
        try (Connection connection = dataSource.getConnection()) {
            log.info("Database connection test: " + connection.getCatalog());
            return true;
        } catch (SQLException e) {
            log.severe("DB Connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a user to the database using a prepared statement.
     *
     * @param user The user to be added to the database.
     * @return True if the operation is successful, false otherwise.
     */
    public boolean addUserToDB(User user) {
        String sql = "INSERT INTO users (user_id, username, email, created_at, last_login, balance, is_admin, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBigDecimal(6, BigDecimal.ZERO); // Set balance to 0 by default
            statement.setBoolean(7, false); // Set is_admin to false by default
            statement.setString(8, "firebase_managed"); // Placeholder for password_hash

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.severe("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Searches for a user by their ID.
     *
     * @param id The user ID to search for.
     * @return The user if found, or a default user object if not found.
     */
    public User searchForUser(String id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = new User();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getString("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            log.severe("Error searching for user: " + e.getMessage());
        }

        return user;
    }

    /**
     * Disconnects from the database (no-op for DataSource-based connections).
     *
     * @return Always returns true (connection is closed automatically after each use).
     */
    public boolean disconnectFromDB() {
        log.info("No persistent database connection to close.");
        return true; // DataSource handles connection pooling automatically
    }
}