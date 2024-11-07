package com.lms.informationservice.team;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Team class
 * @author Caoimhe Cahill
 */
class TeamTest {

    @Test
    void testGetAndSetTeamID() {
        Team team = new Team();
        int teamID = 100;

        team.setTeamID(teamID);
        assertEquals(teamID, team.getTeamID());
    }

    @Test
    void testGetAndSetTeamName() {
        Team team = new Team();
        String teamName = "Liverpool FC";

        team.setTeamName(teamName);
        assertEquals(teamName, team.getTeamName());
    }

    @Test
    void testGetAndSetTla() {
        Team team = new Team();
        String tla = "LIV";

        team.setTla(tla);
        assertEquals(tla, team.getTla());
    }

    @Test
    void testTeamAllFields() {
        Team team = new Team();
        int teamID = 101;
        String teamName = "Manchester United";
        String tla = "MUN";

        team.setTeamID(teamID);
        team.setTeamName(teamName);
        team.setTla(tla);

        assertEquals(teamID, team.getTeamID());
        assertEquals(teamName, team.getTeamName());
        assertEquals(tla, team.getTla());
    }
}
