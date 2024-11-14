package com.lms.gameservice.model;

import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // User ID from user-service
    

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private boolean isActive; // To track if the player is still in the game

    private String teamPick; // Team picked by the player this week
    private String nextPick; //team for next week

    @ElementCollection
    private ArrayList<String> teamsAvailable = new ArrayList<>();

    @ElementCollection
    private ArrayList<String> teamsUsed = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTeamPick() {
        return teamPick;
    }

    public void setTeamPick(String teamPick) {
        this.teamPick = teamPick;
    }

    public String getNextPick() {
        return nextPick;
    }

    public void setNextPick(String nextPick) {
        this.nextPick = nextPick;
    }

    public ArrayList<String> getTeamsAvailable() {
        return teamsAvailable;
    }

    public void setTeamsAvailable(ArrayList<String> teamsAvailable) {
        this.teamsAvailable = teamsAvailable;
    }

    public ArrayList<String> getTeamsUsed() {
        return teamsUsed;
    }

    public void setTeamsUsed(ArrayList<String> teamsUsed) {
        this.teamsUsed = teamsUsed;
    }
}
