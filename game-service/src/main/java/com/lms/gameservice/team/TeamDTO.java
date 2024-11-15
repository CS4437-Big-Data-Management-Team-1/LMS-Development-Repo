package com.lms.gameservice.team;

/**
 * Data Transfer Object (DTO) for Matches information when getting from Information-service
 *
 * @author Caoimhe Cahill
 */
public class TeamDTO {

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
}
