
package com.lms.gameservice.database;

import com.lms.gameservice.model.Game;
import java.sql.*;
import java.time.LocalDateTime;
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

        String sql = "INSERT INTO lastmanstandinggames (lms_game_id, start_date, entry_fee, creator_id, game_name) VALUES (?, ?, ?, ?, ? )";
        try (PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setInt(1, game.getId());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBigDecimal(3, game.getEntryFee());
            statement.setString(4, uid);
            statement.setString(5, game.getName());


            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error inserting game " + e.getMessage());
            return false;
        }

    }

    public static Game findGameByID(int id){

            Game result = new Game();

            if (connection == null) {
                log.severe("Database connection is null.");
                return result;
            }


            String sql = "SELECT * FROM lastmanstandinggames WHERE lms_game_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, id);
                        try(ResultSet results = statement.executeQuery()){
                            if(results.next()){
                                result.setId(results.getInt("lms_game_id"));
                                result.setName(results.getString("game_name"));
                                result.setStartDate(results.getTimestamp("start_date").toLocalDateTime());
                                result.setEntryFee(results.getBigDecimal("entry_fee"));
                            }
                        }
            }catch (SQLException e){
                log.severe("Error fetching game " + e.getMessage());
            }
            return result;

        }



    public static boolean updateGame(Game game){
        String sql= "UPDATE lastmanstandinggames SET start_date = ?, entry_fee = ?, total_pot=?, game_name = ?  WHERE lms_game_id = ?;";
                try (PreparedStatement statement = connection.prepareStatement(sql)){
                    statement.setTimestamp(1, Timestamp.valueOf(game.getStartDate()));
                    statement.setBigDecimal(2, game.getEntryFee());
                    statement.setBigDecimal(3, game.getTotalPot());
                    statement.setString(4, game.getName());
                    statement.setInt(5, game.getId());

                    int execute = statement.executeUpdate();
                    return true;
                }catch (SQLException e){
                    log.severe("Error fetching game " + e.getMessage());
                    return false;
                }
    }


    public static boolean addUserToGame(int gameId, String uid) throws Exception{
        String sql= "INSERT INTO users.user_game_table (user_id, game_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,uid);
            statement.setInt(2, gameId);

            int execute = statement.executeUpdate();
            return true;
        }catch (SQLException e){
            log.severe("Error adding to game " + e.getMessage());
            throw new Exception("User is already in game");
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