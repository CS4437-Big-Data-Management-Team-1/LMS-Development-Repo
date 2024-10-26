package com.lms.informationservice.model;

import com.lms.informationservice.fixture.Fixture;
import com.lms.informationservice.standing.Standing;
import com.lms.informationservice.team.Team;
import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany
    private ArrayList<Team> teams;     // List of teams in the league

    @OneToMany
    private ArrayList<Fixture> fixtures; // List of fixtures

    @OneToMany
    private ArrayList<Team> winTeams; // List of winning teams

    @OneToMany
    private ArrayList<Team> loseDraw; // List of teams that lose or draw

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Standing[] table = new Standing[20]; // League standings

    // Getters and Setters
    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public ArrayList<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(ArrayList<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

    public ArrayList<Team> getWinTeams() {
        return winTeams;
    }

    public void setWinTeams(ArrayList<Team> winTeams) {
        this.winTeams = winTeams;
    }

    public ArrayList<Team> getLoseDraw() {
        return loseDraw;
    }

    public void setLoseDraw(ArrayList<Team> loseDraw) {
        this.loseDraw = loseDraw;
    }

    public Standing[] getTable() {
        return table;
    }

    public void setTable(Standing[] table) {
        this.table = table;
    }
}
