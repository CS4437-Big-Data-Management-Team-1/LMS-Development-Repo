package com.lms.informationservice.team;

import jakarta.persistence.*;

/**
 * Entity class representing a team in the database.
 * This class maps to a database table that stores information about individual teams.
 * Contains fields for team ID, team name and their three letter acronym.
 *
 * Basic getters and setters are provided for managing team data.
 *
 * @author Caoimhe Cahill
 */
@Entity
public class Team {

    @Id
    private int teamID;
    private String teamName;
    private String tla;

    // Getters and Setters
    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTla() {
        return tla;
    }

    public void setTla(String tla) {
        this.tla = tla;
    }


    public String toString() {
        return "Team{" +
                "teamID=" + teamID +
                ", teamName='" + teamName + '\'' +
                ", tla='" + tla + '\'' +
                '}';
    }
}

