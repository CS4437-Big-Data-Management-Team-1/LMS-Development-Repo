package com.lms.gameservice.matches;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MatchesDTO class.
 *
 * @author Caoimhe Cahill
 */
class MatchesDTOTests {

    @Test
    void testGameID() {
        MatchesDTO matchesDTO = new MatchesDTO();
        int expectedGameID = 101;
        matchesDTO.setGameID(expectedGameID);
        int actualGameID = matchesDTO.getGameID();
        assertEquals(expectedGameID, actualGameID);
    }

    @Test
    void testHomeTeamID() {
        MatchesDTO matchesDTO = new MatchesDTO();
        int expectedHomeTeamID = 201;
        matchesDTO.setHomeTeamID(expectedHomeTeamID);
        int actualHomeTeamID = matchesDTO.getHomeTeamID();
        assertEquals(expectedHomeTeamID, actualHomeTeamID);
    }

    @Test
    void testAwayTeamID() {
        MatchesDTO matchesDTO = new MatchesDTO();
        int expectedAwayTeamID = 202;
        matchesDTO.setAwayTeamID(expectedAwayTeamID);
        int actualAwayTeamID = matchesDTO.getAwayTeamID();
        assertEquals(expectedAwayTeamID, actualAwayTeamID);
    }

    @Test
    void testHomeTeamName() {
        MatchesDTO matchesDTO = new MatchesDTO();
        String expectedHomeTeamName = "Warriors";
        matchesDTO.setHomeTeamName(expectedHomeTeamName);
        String actualHomeTeamName = matchesDTO.getHomeTeamName();
        assertEquals(expectedHomeTeamName, actualHomeTeamName);
    }

    @Test
    void testAwayTeamName() {
        MatchesDTO matchesDTO = new MatchesDTO();
        String expectedAwayTeamName = "Tigers";
        matchesDTO.setAwayTeamName(expectedAwayTeamName);
        String actualAwayTeamName = matchesDTO.getAwayTeamName();
        assertEquals(expectedAwayTeamName, actualAwayTeamName);
    }

    @Test
    void testGameDate() {
        MatchesDTO matchesDTO = new MatchesDTO();
        Date expectedGameDate = new Date();
        matchesDTO.setGameDate(expectedGameDate);
        Date actualGameDate = matchesDTO.getGameDate();
        assertEquals(expectedGameDate, actualGameDate);
    }

    @Test
    void testResult() {
        MatchesDTO matchesDTO = new MatchesDTO();
        String expectedResult = "Home Win";
        matchesDTO.setResult(expectedResult);
        String actualResult = matchesDTO.getResult();
        assertEquals(expectedResult, actualResult);
    }
}
