package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.model.Results;
import com.lms.gameservice.repository.GameRepository;
import java.time.DayOfWeek;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.repository.PlayerRepository;
import com.lms.gameservice.repository.ResultsRepository;
import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.team.Team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final ResultsRepository resultsRepository;
    private final PaymentServiceClient paymentService;

    private final GameDatabaseController db = new GameDatabaseController();
    private final InformationServiceClient info;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, ResultsRepository resultsRepository, PaymentServiceClient paymentService, InformationServiceClient info) {
        db.connectToDB();
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.resultsRepository = resultsRepository;
        this.paymentService = paymentService;
        this.info = info;
    }

    /**
     * Create a new game
     * 
     * @param name name of the game
     * @param entryFee entry fee for the game
     * @param weeksTillStartDate number of weeks until the game starts (from next Monday)
     * @param uid user id of the game creator
     * @return the created game
     */
    public Game createGame(String name, BigDecimal entryFee, int weeksTillStartDate, String uid) {

        Game game = new Game();
        game.setName(name);
        game.setEntryFee(entryFee);

        LocalDateTime today = LocalDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(1);
        
        LocalDateTime nextMonday = today.with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDateTime startDate = nextMonday.plusWeeks(weeksTillStartDate);
        game.setStartDate(startDate);
        game.setStatus("CREATED");
        game.setTotalPot(BigDecimal.ZERO);
        // fillTeams(game);
        db.addGameToDB(game, uid);
        return gameRepository.save(game);
    }

    /**
     * Join a game
     * 
     * @param gameId id of the game
     * @param uid user id of the player
     * @param token payment token
     * @return true if the player successfully joined the game
     * @throws Exception if the player cannot join the game
     */
    public boolean joinGame(int gameId, String uid, String token) throws Exception {
        Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        
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
        player.setUserId(uid);

        List<Team> teams = info.getTeamsFromDatabase();
        ArrayList<String> teamsAvailable = new ArrayList<String>();
        player.setTeamsUsed(teamsAvailable);
        for(Team team: teams){
            teamsAvailable.add(team.getTeamName());
        }
        player.setTeamsAvailable(teamsAvailable);
        
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

    /**
     * Get all joinable games
     * @return list of joinable games
     */
    public List<Game> getJoinableGames() {
        // Retrieve games where startDate is in the future
        return gameRepository.findJoinableGames(LocalDateTime.now());
    }

    /**
     * Start a game on the start date
     * @param gameId id of the game
     */
    public void startGame(int gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        game.setStatus("ACTIVE");
        game.setCurrentRoundStartDate(LocalDateTime.now());
        game.setCurrentRoundEndDate(LocalDateTime.now().plusDays(6));
        // fillTeams(game);
        gameRepository.save(game);
    }

    /**
     * process the current round of a game
     * @param gameId id of the game
     */
    public void nextRound(int gameId) { 

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        System.out.println("Processing round for game: " + game.getName());
        updatePlayers(game);
        game.setCurrentRoundStartDate(game.getCurrentRoundEndDate().plusDays(1));
        game.setCurrentRoundEndDate(game.getCurrentRoundStartDate().plusDays(6));
        game.setCurrentRound(game.getCurrentRound() + 1);
        gameRepository.save(game);

    }

    /**
     * update the all players status based on the results of the current round
     * @param game the game to update
     */
    public void updatePlayers(Game game) {
        
        Results results = resultsRepository.findLatestResult();
        System.out.println("Results: " + results.getWinners());
        ArrayList<String> winners = results.getWinners();
        System.out.println("Winners: " + winners);

        List<Player> activePlayers = playerRepository.findByGameAndIsActive(game, true);
        for (Player player : activePlayers) {
            if (!winners.contains(player.getTeamPick())) {
                player.setActive(false);
                playerRepository.save(player);
            }
        }
        
    }

    /**
     * Print the next week's match fixtures
     * @param gameId id of the game
     */
    public void printNextWeeksMatches(int gameId) {
        
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedStartDate = game.getCurrentRoundEndDate().plusDays(1).format(formatter);
        String formattedEndDate = game.getCurrentRoundEndDate().plusDays(8).format(formatter);

        List<Matches> matches = info.fetchMatchesWithinDateRange(formattedStartDate, formattedEndDate);
        for(Matches m: matches) {
            System.out.println(m.getHomeTeamName() + " vs " + m.getAwayTeamName() + " on " + m.getGameDate());
        }
    }

}