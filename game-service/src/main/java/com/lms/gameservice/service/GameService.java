package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.repository.PlayerRepository;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.service.PaymentServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PaymentServiceClient paymentService;

    private final GameDatabaseController db = new GameDatabaseController();
    //TODO  Will need payment service here probs notification too

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, PaymentServiceClient paymentService) {
        db.connectToDB();
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.paymentService = paymentService;

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

    public boolean joinGame(int gameId, String uid, String token) throws Exception {
        Game game = db.findGameByID(gameId);

        boolean paidSuccessfully = paymentService.makePayment(game.getEntryFee(), gameId, token);
        if(!paidSuccessfully){
            throw new Exception("Payment not complete");
        }
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

        try{
        boolean added = db.addUserToGame(gameId, uid);
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