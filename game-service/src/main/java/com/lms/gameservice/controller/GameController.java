package com.lms.gameservice.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.gameservice.gamerequest.GameRequestDTO;
import com.lms.gameservice.matches.MatchesDTO;
import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.model.Results;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.repository.ResultsRepository;
import com.lms.gameservice.service.AuthService;
import com.lms.gameservice.service.GameService;
import com.lms.gameservice.service.InformationServiceClient;
import com.lms.gameservice.service.NotificationServiceClient;
import com.lms.gameservice.service.PlayerService;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final GameRepository gameRepository;

    //for results testing
    private final InformationServiceClient info;
    private final ResultsRepository resultsRepository;

    private final NotificationServiceClient noti;

    @Autowired
    private AuthService authService;

    @Autowired
    public GameController(GameService gameService, PlayerService playerService, GameRepository gameRepository, InformationServiceClient info, ResultsRepository resultsRepository, NotificationServiceClient noti) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.gameRepository = gameRepository;
        this.info = info;
        this.resultsRepository = resultsRepository;
        this.noti = noti;
    }

    /**
     * Endpoint to create a new game.
     * creates Game and stores it in the database table 'games'
     * 
     * @param gameRequest DTO containing the game name, entry fee and weeks till start date
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGame(
            @RequestHeader("Authorisation") String authorisationHeader,
            @RequestBody GameRequestDTO gameRequest) {

        // Verify Firebase ID token for user authentication (Wont work till user id changed to UID
        try {
            String uid = authService.validateToken(authorisationHeader);

            String userID = noti.extractUidFromMessage(uid);

            String userEmail = noti.getUserEmailByUid(userID);

            // Step 2: Create the game with the start date
            Game game = gameService.createGame(gameRequest.getName(), gameRequest.getEntryFee(),
                    gameRequest.getWeeksTillStartDate(), uid);

            noti.sendGameCreationNotification(userEmail, "game_created", gameRequest.getName(), String.valueOf(gameRequest.getWeeksTillStartDate()), String.valueOf(gameRequest.getEntryFee().doubleValue()));
            System.out.print(userEmail);
            return ResponseEntity.ok(game);

        } catch (Exception e) {
            // Handle any Firebase authentication errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error creating game: " + e);
        }
    }

    /**
     * Endpoint to show all joinable games.
     * games are joinable if the start date is in the future.
     * 
     */
    @GetMapping("/joinable")
    public ResponseEntity<List<Game>> getJoinableGames() {
        List<Game> joinableGames = gameService.getJoinableGames();
        return ResponseEntity.ok(joinableGames);
    }


    /**
     * Endpoint to allow a user to join a game
     * 
     * @param game_id the id of the game the user will attempt to join
     */
    @PostMapping("/{game_id}/join")
    public ResponseEntity<String> joinGame(
            @RequestHeader("Authorisation") String authorisationHeader,
            @PathVariable("game_id") int gameId) {

        // Verify Firebase ID token for user authentication
        try {
            String msg = authService.validateToken(authorisationHeader);
            String[] splits = msg.split("Access granted for user: ");
            String uid = splits[1];

            String userEmail = noti.getUserEmailByUid(uid);

            Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

            double entryFee = game.getEntryFee().doubleValue();

            String gameName = game.getName();

            // Step 2: Attempt to join the game

            boolean joinedSuccessfully = gameService.joinGame(gameId, uid, authorisationHeader);
            if (joinedSuccessfully) {
                System.out.println("User has joined game " +  gameId + " successfully.");
                noti.sendGameJoinedNotification(userEmail, "game_joined", gameName, String.valueOf(entryFee));
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



    /**
     * Endpoint to allow a user to pick a team for next week's matches
     * 
     * @param game_id the id of the game the user will pick their team,
     *  (not game here is Game object not soccer game)
     */
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
            if(player.getNextPick() == null) {
                playerService.pickTeam(player, teamStr);
            } else {
                playerService.changeTeamPick(player, teamStr);
            }
            

            return ResponseEntity.ok("Team " + team + " picked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error picking team: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    /**
     * Endpoint to allow a user change their team  
     * 
     * @param game_id the id of the Game
     */
    @PostMapping("/{game_id}/changeTeamPick")
    public ResponseEntity<String> changeTeam(
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
            playerService.changeTeamPick(player, teamStr);

            return ResponseEntity.ok("Team " + team + " picked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error picking team: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/{game_id}/availableTeams")
    public ResponseEntity<?> getAvailableTeams(
            @RequestHeader("Authorisation") String authorisationHeader,
            @PathVariable("game_id") int gameId) {

        try {
            String msg = authService.validateToken(authorisationHeader);
            String[] splits = msg.split("Access granted for user: ");
            String uid = splits[1]; 

            Player player = playerService.getPlayerByGameIdAndUserId(gameId, uid);

            if (player == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Player not found in the game.");
            }

            ArrayList<String> availableTeams = player.getTeamsAvailable();

            if (availableTeams == null || availableTeams.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No available teams for this player.");
            }

            return ResponseEntity.ok(availableTeams);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error verifying the player: " + e.getMessage());
        }
    }

    @GetMapping("/{game_id}/usedTeams")
    public ResponseEntity<?> getUsedTeams(
            @RequestHeader("Authorisation") String authorisationHeader,
            @PathVariable("game_id") int gameId) {

        try {
            String msg = authService.validateToken(authorisationHeader);
            String[] splits = msg.split("Access granted for user: ");
            String uid = splits[1]; 

            Player player = playerService.getPlayerByGameIdAndUserId(gameId, uid);

            if (player == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Player not found in the game.");
            }

            ArrayList<String> availableTeams = player.getTeamsUsed();

            if (availableTeams == null || availableTeams.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No available teams for this player.");
            }

            return ResponseEntity.ok(availableTeams);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error verifying the player: " + e.getMessage());
        }
    }
    
    /**
     * Used to test the Results Table is working.
     * 
     */
    @PostMapping("/resultTest")
    public void uploadResultsTest() {
        
    LocalDate today = LocalDate.now();
    
    LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate lastSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    lastSunday = lastSunday.plusDays(1);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String startDate = lastMonday.format(formatter);
    String endDate = lastSunday.format(formatter);
    
    List<MatchesDTO> matches = info.fetchMatchesWithinDateRange(startDate, endDate);

    Results result = new Results();
    ArrayList<String> winners = new ArrayList<>();
    for (MatchesDTO match : matches) {
        winners.add(match.getResult());
    }
    result.setWinners(winners);

    resultsRepository.save(result);
    }

    /**
     * Used to test the rounds working properly.
     * 
     */
    @PostMapping("/updateTest")
    public void updateActiveGames() {
        List<Game> games = gameRepository.findByStatus("ACTIVE");

        for (Game game : games) {
            gameService.nextRound(game.getId());
        }
    }

    /**
     * Used to test the game start working properly.
     * 
     */
    @PostMapping("/{game_id}/gameStartTest")
    public void startGame(@PathVariable("game_id") int gameId) {
        
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

                gameService.startGame(game.getId());
    }
}
