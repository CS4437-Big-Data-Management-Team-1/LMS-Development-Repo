package com.lms.gameservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import jakarta.persistence.*;
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private BigDecimal entryFee;
    private LocalDateTime startDate;
    private String status;
    private BigDecimal totalPot = BigDecimal.ZERO;

    private ArrayList<Player> playersStillStanding = new ArrayList<>();
    private ArrayList<Player> playersEliminated = new ArrayList<>();

    private LinkedHashMap<Integer, String> teamNames = new LinkedHashMap<>();
    private HashMap<String, Boolean> results = new HashMap<>();

    private int currentRound;
    private LocalDateTime currentRoundStartDate;
    private LocalDateTime currentRoundEndDate;



    public BigDecimal getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(BigDecimal totalPot) {
        this.totalPot = totalPot;
    }

    // Getters and setters

    public int getId(){
        return id;
    }


    public String getName() {
        return name;
    }
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addPlayer(Player player) {
        this.playersStillStanding.add(player);
    }
    
    public void eliminatePlayer(Player player) {
        this.playersStillStanding.remove(player);
        this.playersEliminated.add(player);
    }

    public HashMap<Integer, String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(LinkedHashMap<Integer, String> teamNames) {
        this.teamNames = teamNames;
    }

    public HashMap<String, Boolean> getResults() {
        return results;
    }

    public void setResults(HashMap<String, Boolean> results) {
        this.results = results;
    }

    public ArrayList<Player> getPlayersStillStanding() {
        return playersStillStanding;
    }

    public void setPlayersStillStanding(ArrayList<Player> playersStillStanding) {
        this.playersStillStanding = playersStillStanding;
    } 

    public ArrayList<Player> getPlayersEliminated() {
        return playersEliminated;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public LocalDateTime getCurrentRoundStartDate() {
        return currentRoundStartDate;
    }

    public void setCurrentRoundStartDate(LocalDateTime currentRoundStartDate) {
        this.currentRoundStartDate = currentRoundStartDate;
    }

    public LocalDateTime getCurrentRoundEndDate() {
        return currentRoundEndDate;
    }

    public void setCurrentRoundEndDate(LocalDateTime currentRoundEndDate) {
        this.currentRoundEndDate = currentRoundEndDate;
    }
}
