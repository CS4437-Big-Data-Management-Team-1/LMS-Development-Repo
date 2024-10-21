package com.lms.userservice.database;

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
     * Pulls data from the database.properties file.
     * driver manager handles connection
     *
     * @return          The boolean value of if the connection succeeded
     *
     */
    public static boolean connectToDB(){

        log.info("Loading application properties");
        Properties properties = new Properties();
        try {
            properties.load(UserDatabaseConnector.class.getClassLoader().getResourceAsStream("database.properties"));
        } catch (IOException e){
            log.severe("Error loading properties");
            return false;
        }


        log.info("Connecting to the database");
        try{
            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
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
    public static boolean addUserToDB(){

        //Sample data for now, replace with actual data pulled from user input later
        String sql = "INSERT INTO users (username,email,password,created_at,last_login,balance) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, "John" );
            statement.setString(2, "abcde@gmail.com");
            statement.setString(3, "abcde");
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBigDecimal(6, BigDecimal.TEN);

            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting user " + e.getMessage());
            return false;
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