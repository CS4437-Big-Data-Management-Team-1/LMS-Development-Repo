package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.config.Config;
import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.repository.PlayerRepository;
import com.lms.informationservice.matches.Matches;
import com.lms.informationservice.team.Team;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import com.lms.gameservice.database.GameDatabaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameDatabaseController db = new GameDatabaseController();
    private final InformationServiceClient info;
    //TODO  Will need payment service here probs notification too

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, InformationServiceClient info) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.info = info;
    }

    public Game createGame(String name, BigDecimal entryFee, LocalDateTime startDate, String uid) {
        db.connectToDB();
        Game game = new Game();
        game.setName(name);
        game.setEntryFee(entryFee);
        game.setStartDate(startDate);
        game.setStatus("CREATED");
        game.setTotalPot(BigDecimal.ZERO);
        game.setTeamNames(new LinkedHashMap<Integer, String>());
        fillTeams(game);
        db.addGameToDB(game, uid);
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

        game.addPlayer(player);

        return true;
    }

    public List<Game> getJoinableGames() {
        // Retrieve games where startDate is in the future
        return gameRepository.findJoinableGames(LocalDateTime.now());
    }

    public void fillTeams(Game game) {
        
        List<Team> teams = info.fetchTeams();
        System.out.println("Teams fetched from information service = " + teams.size());

        for (Team team : teams) {
            game.getTeamNames().put(team.getTeamID(), team.getTeamName());
            game.getResults().put(team.getTeamName(), true);
        }
        System.out.println("Teams added to game" + game.getTeamNames().size());
    }

    public void startGame(Long gameId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        game.setStatus("ACTIVE");
        game.setCurrentRoundStartDate(LocalDateTime.now());
        game.setCurrentRoundEndDate(LocalDateTime.now().plusDays(6));
        fillTeams(game);
        gameRepository.save(game);
    }

    public boolean nextRound(Long gameId) { 

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        updateResults(game);
        updatePlayers(game);
        game.setCurrentRoundStartDate(game.getCurrentRoundEndDate().plusDays(1));
        game.setCurrentRoundEndDate(game.getCurrentRoundStartDate().plusDays(6));
        game.setCurrentRound(game.getCurrentRound() + 1);
        gameRepository.save(game);

        return true;
    }

    public void updatePlayers(Game game) {
        
        for(Player player : game.getPlayersStillStanding()) {
            String teamPick = player.getTeamPick();

            if(!game.getResults().get(teamPick)) {
            
                game.eliminatePlayer(player);
            }

            
            player.setTeamPick(player.getNextPick());
            player.setNextPick(null);
            playerRepository.save(player);
        }
        gameRepository.save(game);

    }

    public void updateResults(Game game) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedStartDate = game.getCurrentRoundStartDate().format(formatter);
        String formattedEndDate = game.getCurrentRoundEndDate().format(formatter);

        List<Matches> matches = info.fetchMatchesWithinDateRange(formattedStartDate, formattedEndDate);
        for(Matches m: matches) {
            String winner = m.getResult();
            if(winner.equals("DRAW")) {
                game.getResults().put(m.getHomeTeamName(), false);
                game.getResults().put(m.getAwayTeamName(), false);

            } else if (winner.equals(m.getHomeTeamName())) {
                game.getResults().put(m.getHomeTeamName(), true);
                game.getResults().put(m.getAwayTeamName(), false);

            } else {
                game.getResults().put(m.getHomeTeamName(), false);
                game.getResults().put(m.getAwayTeamName(), true);
            }
        }

        gameRepository.save(game);
    }

    public void eliminatePlayer(Game game, Player player) {
    
        player.setActive(false);
        game.getPlayersStillStanding().remove(player);
        game.getPlayersEliminated().add(player);
    
        gameRepository.save(game);
    }

    
    public void printNextWeeksMatches(Long gameId) {
        
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