package com.lms.gameservice.database;

import com.lms.gameservice.service.GameService;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.io.IOException;
import java.math.BigDecimal;
import com.lms.gameservice.model.Game;
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

    public static boolean addGameToDB( Game game, String token){
        String[] splits = token.split("Access granted for user: ");
        String uid = splits[1];

        String sql = "INSERT INTO lastmanstandinggames (lms_game_id, start_date, entry_fee, creator_id) VALUES (?, ?, ?, ? )";
        try (PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, game.getId());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBigDecimal(3, game.getEntryFee());
            statement.setString(4, uid);


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