package com.lms.informationservice.matches;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Entity class representing a match in the database.
 * This class maps to a database table that stores information about individual matches.
 * Contains fields for game ID, home team ID, away team ID, date and time of match and the match result.
 *
 * Basic getters and setters are provided for managing match data.
 *
 * @author Caoimhe Cahill
 */
@Entity
public class Matches {

    @Id
    private int gameID;
    private int homeTeamID;
    private int awayTeamID;
    private Date gameDate;
    private String result;

    // Getters and Setters
    public int getId() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getHomeTeamID() {
        return homeTeamID;
    }

    public void setHomeTeamID(int homeTeamID) {
        this.homeTeamID = homeTeamID;
    }

    public int getAwayTeamID() {
        return awayTeamID;
    }

    public void setAwayTeamID(int awayTeamID) {
        this.awayTeamID = awayTeamID;
    }

    public Date getGameDate(){
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
