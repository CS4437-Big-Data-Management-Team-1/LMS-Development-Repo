package com.lms.gameservice.controller;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.service.GameService;
import com.lms.gameservice.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;
    private final RoundService roundService;

    @Autowired
    public GameController(GameService gameService, RoundService roundService) {
        this.gameService = gameService;
        this.roundService = roundService;
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        return ResponseEntity.ok(gameService.createGame(game));
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<String> joinGame(@PathVariable Long gameId, @RequestParam Long userId) {
        gameService.joinGame(gameId, userId);
        //TODO payment
        return ResponseEntity.ok("User joined game " + gameId);
    }

    @PostMapping("/{gameId}/round/{roundId}/process")
    public ResponseEntity<String> processRound(@PathVariable Long gameId, @PathVariable Long roundId, @RequestBody List<Long> winningTeamIds) {

        //TODO logic
        return ResponseEntity.ok("Round results processed for game " + gameId);
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllAvailableGames() {

        //TODO get all games
        return null;
    }
}

}