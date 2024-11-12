package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.GameRepository;
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

    //TODO  Will need payment service here probs notification too

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;

    }

    public Game createGame(String name, BigDecimal entryFee, LocalDateTime startDate) {
        Game game = new Game();
        game.setName(name);
        game.setEntryFee(entryFee);
        game.setStartDate(startDate);
        game.setStatus("CREATED");

        return gameRepository.save(game);
    }

    public boolean joinGame(Long gameId, String uid) {
        // Retrieve the game to ensure it’s joinable
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        // Check if the game has already started
        if (game.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot join a game that has already started.");
        }

        // TODO Handle payment
//        boolean paymentSuccessful = paymentService.processEntryFee(uid, game.getEntryFee());
//        if (!paymentSuccessful) {
//            return false;
//        }

        // Add the user to the game
        Player player = new Player();
        // player.setUserId(uid); can fix when user id takes in string
        player.setGame(game);
        player.setActive(true);
        playerRepository.save(player);

        // Update the total pot in the game
        game.setTotalPot(game.getTotalPot().add(game.getEntryFee()));
        gameRepository.save(game);

        return true;
    }

    public List<Game> getJoinableGames() {
        // Retrieve games where startDate is in the future
        return gameRepository.findJoinableGames(LocalDateTime.now());
    }


}