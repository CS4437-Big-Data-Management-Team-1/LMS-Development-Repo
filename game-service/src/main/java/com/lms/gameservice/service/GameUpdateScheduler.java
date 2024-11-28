package com.lms.gameservice.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lms.gameservice.controller.GameController;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.matches.MatchesDTO;
import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.model.Results;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.repository.ResultsRepository;

@Component
public class GameUpdateScheduler {

    private final GameService gameService;
    private final GameRepository gameRepository;
    private final ResultsRepository resultsRepository;
    private final InformationServiceClient info;
    private final GameDatabaseController db = new GameDatabaseController();
    private final RestTemplate restTemplate;
    private static final Logger logger = LogManager.getLogger(GameController.class);


    @Autowired
    public GameUpdateScheduler(GameService gameService, GameRepository gameRepository, ResultsRepository resultsRepository, InformationServiceClient info, RestTemplate restTemplate) {
        db.connectToDB();
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.resultsRepository = resultsRepository;
        this.info = info;
        this.restTemplate = restTemplate;
    }

    /**
     * every Monday at 1am, update the active games
     */
    @Scheduled(cron = "0 0 1 * * MON")
    public void updateActiveGames() {
        List<Game> games = gameRepository.findByStatus("ACTIVE");

        for (Game game : games) {
            gameService.nextRound(game.getId());

            // Retrieve active players in the game
            List<Player> players = gameService.getActivePlayersInGame(game.getId());

            // Send notifications to all players with individual game and player values
            for (Player player : players) {
                String userID = player.getUserId();
                String userEmail = getUserEmailByUid(userID);

                if (userEmail != null) {
                    sendGameUpdateNotification(
                        userEmail,
                        "game_update",
                        game.getName(),
                        game.getCurrentRound(),
                        game.getCurrentRoundStartDate().toString(),
                        game.getCurrentRoundEndDate().toString(),
                        game.getTotalPot().toString(),
                        player.isActive() ? "Active" : "Eliminated",
                        player.getTeamPick()
                    );
                } else {
                    logger.warn("No email found for user ID: {}", userID);
                }
            }
        }
    }



    /**
     * every Monday at 2am, check if any games need to be started
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void checkFutureGames() {
        List<Game> games = gameRepository.findByStatus("CREATED");

        for (Game game : games) {
            if(game.getStartDate().isBefore(java.time.LocalDateTime.now())){
                gameService.startGame(game.getId());
            }
        }
    }

    /**
     * every Monday at 1am, upload the results of the previous week
     */
    @Scheduled(cron = "0 0 1 * * MON")
    public void uploadResults() {
        
    LocalDate today = LocalDate.now();
    
    LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate lastSunday = today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).minusDays(1);
    
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

    public void sendGameUpdateNotification(
        String recipient,
        String type,
        String gameName,
        int currentRound,
        String roundStartDate,
        String roundEndDate,
        String totalPot,
        String playerStatus,
        String playerTeamPick
    ) {
        String notificationUrl = "http://localhost:8085/api/notifications/send";

        // Build the notification data as a map of individual fields
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("recipient", recipient);
        notificationData.put("type", type);
        notificationData.put("gameName", gameName);
        notificationData.put("currentRound", String.valueOf(currentRound));
        notificationData.put("roundStartDate", roundStartDate);
        notificationData.put("roundEndDate", roundEndDate);
        notificationData.put("totalPot", totalPot);
        notificationData.put("playerStatus", playerStatus);
        notificationData.put("playerTeamPick", playerTeamPick);

        try {
            restTemplate.postForEntity(notificationUrl, notificationData, String.class);
            logger.info("Notification sent to {} for game {} (type: {})", recipient, gameName, type);
        } catch (Exception e) {
            logger.error("Failed to send notification to {} for game {}. Error: {}", recipient, gameName, e.getMessage());
        }
    }

    

    public String getUserEmailByUid(String uid) {
        // Call the UserController's endpoint to get the email
        String url = "http://localhost:8080/api/users/" + uid + "/email";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody(); // Return the email
            } else {
                return null; // Handle the case when the user is not found
            }
        } catch (Exception e) {
            // Handle errors
            return null;
        }
    }
    
}