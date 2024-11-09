package com.lms.informationservice.database;

import com.lms.informationservice.team.Team;
import java.util.List;
import java.sql.*;
import jakarta.persistence.*;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class responsible for controlling database operations.
 * This class connects to the database and has mathods for modifying/adding to the teams database specifically
 *
 * @author Mark Harrison
 */
@Entity
public class InformationDatabaseController{

    private static final Logger log;
    private static Connection connection;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log =Logger.getLogger(InformationDatabaseController.class.getName());
    }

    /**
     * Connects to the database
     * Pulls data from the database.properties file.
     * driver manager handles connection
     *
     * @return          The boolean value of if the connection succeeded
     *
     */
    private final static String dbUsername = System.getProperty("DB_USERNAME");
    private final static String dbPassword = System.getProperty("DB_PASSWORD");
    public static boolean connectToDB(){
            log.info("Loading application properties");

        Properties properties = new Properties();
        try {
            properties.load(InformationDatabaseController.class.getClassLoader().getResourceAsStream("database.properties"));
        } catch (IOException e){
            log.severe("Error loading properties");
            return false;
        }


        log.info("Connecting to the database");
        try{
            connection = DriverManager.getConnection(properties.getProperty("url"), dbUsername ,dbPassword);
            log.info("Database connection test: " + connection.getCatalog());
            return true;
        } catch (SQLException e){
            log.severe("DB Connection failed");
            return false;
        }
    }
    /**
     * Adds a team object to the database in the teams table.
     *
     *
     * @return boolean representing if the operation was successful
     */
    @Entity
    public static boolean addTeamsToDB(Team team){
        if (connection == null) {
            log.severe("Database connection is null.");
            return false;
        }


        String sql = "INSERT INTO teams (team_id,team_name,abbreviation) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, team.getTeamID() );
            statement.setString(2, team.getTeamName());
            statement.setString(3, team.getTla());

            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting team " + e.getMessage());
            return false;
        }

    }


}