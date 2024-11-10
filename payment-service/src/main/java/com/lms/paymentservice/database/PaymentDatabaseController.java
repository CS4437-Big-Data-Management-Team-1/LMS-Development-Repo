package com.lms.paymentservice.database;

import com.lms.paymentservice.model.PaymentResponse;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class PaymentDatabaseController{

    private static final Logger log;
    private static Connection connection;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log =Logger.getLogger(PaymentDatabaseController.class.getName());
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
    public static boolean addPaymentToDB(PaymentResponse response){

        //Sample data for now, replace with actual data pulled from user input later
        String sql = "INSERT INTO payments (user_id, amount, payment_date,payment_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, 4 );
            statement.setLong(2, Long.parseLong(response.getAmount()));
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            if(response.isSuccess()) {
                statement.setString(4, "COMPLETED");
            }else{
                statement.setString(4, "FAILED");
            }
            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting payment " + e.getMessage());
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