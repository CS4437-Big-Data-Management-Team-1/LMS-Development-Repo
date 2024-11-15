package com.lms.gameservice.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.PlayerRepository;

@Service
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    private final GameDatabaseController db = new GameDatabaseController();

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;

    }

    /**
     * Allows a player to pick a team for the next round.
     * @param player The player object
     * @param team The team to pick
     */
    public void pickTeam(Player player, String team){

        ArrayList<String> availableTeams = player.getTeamsAvailable();
        if (availableTeams.contains(team)) {
            System.out.println("Available Teams before pick: " + availableTeams);
            availableTeams.remove(team);
            player.setTeamsAvailable(availableTeams);
            System.out.println();
            System.out.println("Available Teams after pick: " + availableTeams);
            System.out.println();

            if(player.getTeamsUsed() == null){
                player.setTeamsUsed(new ArrayList<>());
            }
            ArrayList<String> usedTeams = player.getTeamsUsed();
            usedTeams.add(team);
            player.setTeamsUsed(usedTeams);
            System.out.println("Used Teams after pick: " + usedTeams);

            player.setNextPick(team);
            
            playerRepository.save(player);

        } else {
            throw new IllegalArgumentException("Team not available for pick.");
        }
    }

    /**
     * Allows a player to change their team pick.
     * @param player The player object
     * @param team The team to change to
     */
    public void changeTeamPick(Player player, String newTeam){

        ArrayList<String> usedTeams = player.getTeamsUsed();
        String oldTeam = player.getNextPick();
        if (usedTeams.contains(oldTeam)) {
            usedTeams.remove(oldTeam);
            usedTeams.add(newTeam);
            player.setTeamsUsed(usedTeams);

            // Add the old team back to available teams
            ArrayList<String> availableTeams = player.getTeamsAvailable();
            availableTeams.remove(newTeam);
            System.out.println(newTeam + " removed from available teams: " + availableTeams);
            availableTeams.add(oldTeam);
            player.setTeamsAvailable(availableTeams);

            player.setNextPick(newTeam);
            // Save the updated player object
            playerRepository.save(player);
        } else {
            throw new IllegalArgumentException("Team not found in used teams.");
        }
    }

    /**
     * Allows a player to view their available team picks.
     * @param player The player object
     */
    public void printAvailableTeams(Player player){
        System.out.println("Available Teams: ");
        int index = 1;
        for(String team : player.getTeamsAvailable()){
            System.out.println("(" + index + ") " + team);
            index++;
        }
    }

    
    /**
     * Get a player by their gameId and userId
     * @param gameId The id of the game
     * @param userId The id of the user
     * @return The player object
     */
    public Player getPlayerByGameIdAndUserId(int gameId, String userId) {

        System.out.println("Looking for player with gameId: " + gameId + " and userId: " + userId);
        return playerRepository.findByGameIdAndUserId(gameId, userId);
    }


}
