package com.lms.gameservice.database;

import com.lms.gameservice.service.GameService;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class GameDatabaseController{

    private static final Logger log;
    private static Connection connection;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log =Logger.getLogger(GameDatabaseController.class.getName());
    }


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

    public static boolean addGameToDB( /**Game vars to be added when decided*/){


        String sql = "INSERT INTO lastmanstandinggames (start_date, end_date, entry_fee) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
         //   statement.setDate(); end date of lms game.
         //   statement.setLong(3, Long.parseLong(())); entry fee amount.

            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting game " + e.getMessage());
            return false;
        }

    }


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