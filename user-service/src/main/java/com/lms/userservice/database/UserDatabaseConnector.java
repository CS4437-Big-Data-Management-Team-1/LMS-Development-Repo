package com.lms.userservice.database;

import com.lms.userservice.model.User;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class UserDatabaseConnector{

    private static final Logger log;
    private static Connection connection;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log =Logger.getLogger(UserDatabaseConnector.class.getName());
    }

    /**
     * Connects to the database
     * Pulls data from the system properties
     * driver manager handles connection
     *
     * @return          The boolean value of if the connection succeeded
     *
     */
    public static boolean connectToDB(){




        log.info("Connecting to the database");
        try{
            String dbUsername = System.getProperty("DB_USERNAME");
            String dbPassword = System.getProperty("DB_PASSWORD");
            String dbUrl = System.getProperty("DB_USERS_URL");
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            log.info("Database connection test: " + connection.getCatalog());
            return true;
        } catch (SQLException e){
            log.severe("DB Connection failed");
            return false;
        }
    }
/**
 * PreparedStatement.executeUpdate() handles adding rows to the database users
 *
 * @return          returns the boolean status of the function
 */
    public static boolean addUserToDB(User user){

        //Sample data for now, replace with actual data pulled from user input later
        String sql = "INSERT INTO users (user_id, username,email,created_at,last_login) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, user.getId() );
            statement.setString(2, user.getUsername() );
            statement.setString(3, user.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting user " + e.getMessage());
            return false;
        }

    }

    public static User searchForUser(String id){
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = new User();

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, id);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()) {
                    user.setId(resultSet.getString("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                }
                }catch (SQLException e){
                log.severe("Error inserting user " + e.getMessage());
                return user;
            }
            return user;
            }catch (SQLException e){
            log.severe("Error inserting user " + e.getMessage());
            return user;
        }
        }


    /**
     * Disconnects from database
     *
     * @return  boolean value of the operation
     */
    public static boolean disconnectFromDB() {
        if(connection != null) {
            try {
                connection.close();
                log.info("DB Connection Closed");
                return true;
            } catch (SQLException e) {
                log.severe("Failed to disconnect from DB");
                return false;
            }
        }else{
            log.info("No database connection");
            return false;

        }
    }
}