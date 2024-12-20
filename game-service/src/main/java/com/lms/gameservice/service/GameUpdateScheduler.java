package com.lms.gameservice.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private static final Logger logger = LogManager.getLogger(GameController.class);
    private final NotificationServiceClient noti;

    @Autowired
    public GameUpdateScheduler(GameService gameService, GameRepository gameRepository, ResultsRepository resultsRepository, InformationServiceClient info, NotificationServiceClient noti) {
        db.connectToDB();
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.resultsRepository = resultsRepository;
        this.info = info;
        this.noti = noti;
    }

    /**
     * every Monday at 3am, update the active games
     */
    @Scheduled(cron = "0 0 3 * * MON")
    public void updateActiveGames() {
        List<Game> games = gameRepository.findByStatus("ACTIVE");

        for (Game game : games) {
            gameService.nextRound(game.getId());

            // Retrieve active players in the game
            List<Player> players = gameService.getActivePlayersInGame(game.getId());

            // Send notifications to all players with individual game and player values
            for (Player player : players) {
                String userID = player.getUserId();
                String userEmail = noti.getUserEmailByUid(userID);

                if (userEmail != null) {
                    noti.sendGameUpdateNotification(
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
    
}