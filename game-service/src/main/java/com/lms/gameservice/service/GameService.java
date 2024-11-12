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

    public Player joinGame(Long gameId, Long userId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found"));
        //TODO Need to get and pay entry fee here
        Player player = new Player();
        player.setUserId(userId);
        player.setGame(game);
        player.setActive(true);
        return playerRepository.save(player);


//        db.connectToDB();
//        db.addGameToDB(/**added when game vars are decided*/);

    }


}