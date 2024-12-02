package com.lms.gameservice.team;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TeamDTO class.
 *
 * @author Caoimhe Cahill
 */
class TeamDTOTests {

    @Test
    void testTeamID() {
        TeamDTO teamDTO = new TeamDTO();
        int expectedTeamID = 300;
        teamDTO.setTeamID(expectedTeamID);
        int actualTeamID = teamDTO.getTeamID();
        assertEquals(expectedTeamID, actualTeamID);
    }

    @Test
    void testTeamName() {
        TeamDTO teamDTO = new TeamDTO();
        String expectedTeamName = "Team A";
        teamDTO.setTeamName(expectedTeamName);
        String actualTeamName = teamDTO.getTeamName();
        assertEquals(expectedTeamName, actualTeamName);
    }

    @Test
    void testTla() {
        TeamDTO teamDTO = new TeamDTO();
        String expectedTla = "TA";
        teamDTO.setTla(expectedTla);
        String actualTla = teamDTO.getTla();
        assertEquals(expectedTla, actualTla);
    }
}
