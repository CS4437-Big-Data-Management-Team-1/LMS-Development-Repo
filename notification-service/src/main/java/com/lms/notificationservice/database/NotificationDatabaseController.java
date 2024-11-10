package com.lms.notificationservice.database;

import com.lms.notificationservice.model.Notification;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;


public class NotificationDatabaseController{
    /**
     * Class responsible for controlling database operations.
     * This class connects to the database and has mathods for modifying/adding to the games database specifically
     *
     * @author Mark Harrison
     */
    private static final Logger log;
    private static Connection connection;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log =Logger.getLogger(NotificationDatabaseController.class.getName());
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
            String dbUrl = System.getProperty("DB_GAMES_URL");

            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            log.info("Database connection test: " + connection.getCatalog());
            return true;
        } catch (SQLException e){
            log.severe("DB Connection failed");
            return false;
        }
    }
    /**
     * PreparedStatement.executeUpdate() handles adding rows to the database emails
     *
     * @return          returns the boolean status of the function
     */
    public static boolean addEmailToDB(Notification email){

        //Sample data for now, replace with actual data pulled from user input later
        String sql = "INSERT INTO emails ( recipient, subject, time_sent) VALUES ( ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, email.getRecipient());
            statement.setString(2, email.getSubject());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));


            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting email " + e.getMessage());
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