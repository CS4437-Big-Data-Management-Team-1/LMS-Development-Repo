package com.lms.gameservice.controller;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.model.Results;
import com.lms.gameservice.repository.PlayerRepository;
import com.lms.gameservice.repository.ResultsRepository;
import com.lms.gameservice.service.AuthService;
import com.lms.gameservice.service.GameService;
import com.lms.gameservice.service.InformationServiceClient;
import com.lms.gameservice.service.PlayerService;
import com.lms.gameservice.service.RoundService;
import com.lms.informationservice.matches.Matches;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import com.lms.gameservice.gamerequest.GameRequestDTO;

import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    //for results testing
    // private final InformationServiceClient info;
    // private final ResultsRepository resultsRepository;


    @Autowired
    private AuthService authService;

    @Autowired
    public GameController(GameService gameService, PlayerService playerService, PlayerRepository playerRepository) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(
            @RequestHeader("Authorisation") String authorisationHeader,
            @RequestBody GameRequestDTO gameRequest) {

        // Verify Firebase ID token for user authentication (Wont work till user id changed to UID
        try {
            String uid = authService.validateToken(authorisationHeader);

            // Step 2: Create the game with the start date
            Game game = gameService.createGame(gameRequest.getName(), gameRequest.getEntryFee(), gameRequest.getWeeksTillStartDate(), uid);

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
            String msg = authService.validateToken(authorisationHeader);
            String[] splits = msg.split("Access granted for user: ");
            String uid = splits[1];

            // Step 2: Attempt to join the game

            boolean joinedSuccessfully = gameService.joinGame(gameId, uid, authorisationHeader);
            if (joinedSuccessfully) {
                System.out.println("user has joined game " +  gameId + " successfully.");
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

    @PostMapping("/{gameId}/processRound")
    public ResponseEntity<String> processRound(@PathVariable int gameId) {

        boolean roundProcessed = gameService.nextRound(gameId);

        if(roundProcessed) {
            return ResponseEntity.ok("Round reults processed for game " + gameId + " next round started.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to process round.");
        }
    }

    @PostMapping("/{game_id}/pickTeam")
    public ResponseEntity<String> pickTeam(
            @RequestHeader("Authorisation") String authorisationHeader,
            @PathVariable("game_id") int gameId,
            @RequestBody String team) {

        try {
            String msg = authService.validateToken(authorisationHeader);
            String[] splits = msg.split("Access granted for user: ");
            String uid = splits[1];
            
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject teamObject = (JSONObject) parser.parse(team);
            String teamStr = (String) teamObject.get("team");

            Player player = playerService.getPlayerByGameIdAndUserId(gameId, uid);

            if (player == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found in the game.");
            }

            // Call service method to pick the team
            playerService.pickTeam(player, teamStr);

            return ResponseEntity.ok("Team " + team + " picked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error picking team: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

  
    /**
     * Used to test the Results Table is working.
     * 
     */
    // @PostMapping("/resultTest")
    // public void uploadResultsTest() {
        
    // LocalDate today = LocalDate.now();
    
    // LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    // LocalDate lastSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // String startDate = lastMonday.format(formatter);
    // String endDate = lastSunday.format(formatter);
    
    // List<Matches> matches = info.fetchMatchesWithinDateRange(startDate, endDate);

    // Results result = new Results();
    // ArrayList<String> winners = new ArrayList<>();
    // for (Matches match : matches) {
    //     winners.add(match.getResult());
    // }
    // result.setWinners(winners);

    // resultsRepository.save(result);
    // }
}
