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
        // Explicitly call the getter
        int retrievedTeamID = team.getTeamID();
        assertEquals(teamID, retrievedTeamID);
    }

    @Test
    void testGetAndSetTeamName() {
        Team team = new Team();
        String teamName = "Liverpool FC";

        team.setTeamName(teamName);
        // Explicitly call the getter
        String retrievedTeamName = team.getTeamName();
        assertEquals(teamName, retrievedTeamName);
    }

    @Test
    void testGetAndSetTla() {
        Team team = new Team();
        String tla = "LIV";

        team.setTla(tla);
        // Explicitly call the getter
        String retrievedTla = team.getTla();
        assertEquals(tla, retrievedTla);
    }

    @Test
    void testToString() {
        Team team = new Team();
        team.setTeamID(100);
        team.setTeamName("Liverpool FC");
        team.setTla("LIV");

        String expected = "Team{teamID=100, teamName='Liverpool FC', tla='LIV'}";
        assertEquals(expected, team.toString());
    }

}
