package com.lms.gameservice.controller;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.service.AuthService;
import com.lms.gameservice.service.GameService;
import com.lms.gameservice.service.RoundService;
import com.lms.gameservice.gamerequest.GameRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;
    private final RoundService roundService;

    @Autowired
    private AuthService authService;

    @Autowired
    public GameController(GameService gameService, RoundService roundService) {
        this.gameService = gameService;
        this.roundService = roundService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(
            @RequestHeader("Authorisation") String authorisationHeader,
            @RequestBody GameRequestDTO gameRequest) {

        // Verify Firebase ID token for user authentication (Wont work till user id changed to UID
        try {
            String uid = authService.validateToken(authorisationHeader);

            // Step 2: Create the game with the start date
            Game game = gameService.createGame(gameRequest.getName(), gameRequest.getEntryFee(), gameRequest.getStartDate(), uid);

            // Potential to Send Notification game created
            // sendGameCreationNotification(uid, game);

            return ResponseEntity.ok(game);

        } catch (Exception e) {
            // Handle any Firebase authentication errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error creating game: " + e);
        }
    }

    @GetMapping("/joinable")
    public ResponseEntity<List<Game>> getJoinableGames() {
        List<Game> joinableGames = gameService.getJoinableGames();
        return ResponseEntity.ok(joinableGames);
    }

    @PostMapping("/{game_id}/join")
    public ResponseEntity<String> joinGame(
            @RequestHeader("Authorisation") String authorisationHeader,
            @PathVariable("game_id") int gameId) {

        // Verify Firebase ID token for user authentication
        try {
            String uid = authService.validateToken(authorisationHeader);
            // Step 2: Attempt to join the game
            boolean joinedSuccessfully = gameService.joinGame(gameId, uid);

            if (joinedSuccessfully) {
                return ResponseEntity.ok("User joined game " + gameId);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to join the game.");
            }

        }catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any Firebase authentication errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error joining game: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/round/{roundId}/process")
    public ResponseEntity<String> processRound(@PathVariable Long gameId, @PathVariable Long roundId, @RequestBody List<Long> winningTeamIds) {

        //TODO logic
        return ResponseEntity.ok("Round results processed for game " + gameId);
    }
}
