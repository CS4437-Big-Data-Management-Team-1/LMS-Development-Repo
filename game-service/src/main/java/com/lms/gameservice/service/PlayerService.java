package com.lms.gameservice.service;

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

    public void pickTeam(Player player, String team){

        player.setNextPick(team);
        player.getTeamsAvailable().remove(team);
        player.getTeamsUsed().add(team);
        playerRepository.save(player);
    }

    public void changeTeamPick(Player player, String team){

        String previousTeam = player.getNextPick();
        //new pick
        player.setNextPick(team);
        player.getTeamsAvailable().remove(team);
        player.getTeamsUsed().add(team);

        //revert old pick
        player.getTeamsAvailable().add(previousTeam);
        player.getTeamsUsed().remove(previousTeam);

        playerRepository.save(player);
    }

    public void printAvailableTeams(Player player){
        System.out.println("Available Teams: ");
        int index = 1;
        for(String team : player.getTeamsAvailable()){
            System.out.println("(" + index + ") " + team);
            index++;
        }
    }
}
