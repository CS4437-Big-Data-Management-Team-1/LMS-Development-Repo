package com.lms.gameservice.service;

import java.util.ArrayList;
import java.util.Optional;

import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.gameservice.database.GameDatabaseController;
import com.lms.gameservice.model.Game;
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
            System.out.println("here");
            usedTeams.add(team);
            player.setTeamsUsed(usedTeams);
            System.out.println("Used Teams after pick: " + usedTeams);

            playerRepository.save(player);

        } else {
            throw new IllegalArgumentException("Team not available for pick.");
        }
    }

    public void changeTeamPick(Player player, String team){

        ArrayList<String> usedTeams = player.getTeamsUsed();
        
        if (usedTeams.contains(team)) {
            usedTeams.remove(team);
            player.setTeamsUsed(usedTeams);

            // Add the team back to available teams
            ArrayList<String> availableTeams = player.getTeamsAvailable();
            availableTeams.add(team);
            player.setTeamsAvailable(availableTeams);

            // Save the updated player object
            playerRepository.save(player);
        } else {
            throw new IllegalArgumentException("Team not found in used teams.");
        }
    }

    public void printAvailableTeams(Player player){
        System.out.println("Available Teams: ");
        int index = 1;
        for(String team : player.getTeamsAvailable()){
            System.out.println("(" + index + ") " + team);
            index++;
        }
    }

    public Player getPlayerByGameIdAndUserId(int gameId, String userId) {

        System.out.println("Looking for player with gameId: " + gameId + " and userId: " + userId);
        return playerRepository.findByGameIdAndUserId(gameId, userId);
    }


}
