package com.lms.informationservice.matches;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Matches class
 * @author Caoimhe Cahill
 */
class MatchesTest {

    @Test
    void testGetAndSetGameID() {
        Matches match = new Matches();
        int gameID = 1001;

        match.setGameID(gameID);
        assertEquals(gameID, match.getId());
    }

    @Test
    void testGetAndSetHomeTeamID() {
        Matches match = new Matches();
        int homeTeamID = 10;

        match.setHomeTeamID(homeTeamID);
        assertEquals(homeTeamID, match.getHomeTeamID());
    }

    @Test
    void testGetAndSetHomeTeamName() {
        Matches match = new Matches();
        String homeTeamName = "Home Team";

        match.setHomeTeamName(homeTeamName);
        assertEquals(homeTeamName, match.getHomeTeamName());
    }

    @Test
    void testGetAndSetAwayTeamID() {
        Matches match = new Matches();
        int awayTeamID = 20;

        match.setAwayTeamID(awayTeamID);
        assertEquals(awayTeamID, match.getAwayTeamID());
    }

    @Test
    void testGetAndSetAwayTeamName() {
        Matches match = new Matches();
        String awayTeamName = "Away Team";

        match.setAwayTeamName(awayTeamName);
        assertEquals(awayTeamName, match.getAwayTeamName());
    }

    @Test
    void testGetAndSetGameDate() {
        Matches match = new Matches();
        Date gameDate = new Date();

        match.setGameDate(gameDate);
        assertEquals(gameDate, match.getGameDate());
    }

    @Test
    void testGetAndSetResult() {
        Matches match = new Matches();
        String result = "Home Team";

        match.setResult(result);
        assertEquals(result, match.getResult());
    }
}
