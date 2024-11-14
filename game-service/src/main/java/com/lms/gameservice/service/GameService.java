package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.repository.PlayerRepository;
import com.lms.gameservice.database.GameDatabaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameDatabaseController db = new GameDatabaseController();
    //TODO  Will need payment service here probs notification too

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        db.connectToDB();
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;

    }

    public Game createGame(String name, BigDecimal entryFee, LocalDateTime startDate, String uid) {

        Game game = new Game();
        game.setName(name);
        game.setEntryFee(entryFee);
        game.setStartDate(startDate);
        game.setStatus("CREATED");
        db.addGameToDB(game, uid);
        return gameRepository.save(game);
    }

    public boolean joinGame(int gameId, String uid) throws Exception {
        Game game = db.findGameByID(gameId);
        System.out.println(game.getName());


//        // Check if the game has already started
//        if (game.getStartDate().isBefore(LocalDateTime.now())) {
//            throw new IllegalStateException("Cannot join a game that has already started.");
//        }
//


        // Add the user to the game
        Player player = new Player();
        // player.setUserId(uid); can fix when user id takes in string
        player.setGame(game);
        player.setActive(true);
        playerRepository.save(player);

        // Update the total pot in the game

        game.setTotalPot(game.getTotalPot().add(game.getEntryFee()));
        db.updateGame(game);

        //register user to game in user_game_table
        String[] splits = uid.split("Access granted for user: ");
        String userId= splits[1];
        try{
        boolean added = db.addUserToGame(gameId, userId);
        return true;
        }catch(Exception e){
            throw new Exception("User already in game: " + e.getMessage());
        }

    }

    public List<Game> getJoinableGames() {
        // Retrieve games where startDate is in the future
        return gameRepository.findJoinableGames(LocalDateTime.now());
    }


}