package com.lms.gameservice.model;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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

    @Lob
    @Column(name = "teams_available")
    private String teamsAvailableJson;

    @Lob
    @Column(name = "teams_used")
    private String teamsUsedJson;

    private static final ObjectMapper objectMapper = new ObjectMapper();


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
        try {
            return objectMapper.readValue(teamsAvailableJson, ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setTeamsAvailable(ArrayList<String> teamsAvailable) {
        try {
            this.teamsAvailableJson = objectMapper.writeValueAsString(teamsAvailable);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTeamsUsed() {
        try {
            return objectMapper.readValue(teamsUsedJson, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setTeamsUsed(ArrayList<String> teamsUsed) {
        try {
            this.teamsUsedJson = objectMapper.writeValueAsString(teamsUsed);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
