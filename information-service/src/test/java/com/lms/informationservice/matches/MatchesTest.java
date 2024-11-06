package com.lms.informationservice.matches;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testGetAndSetAwayTeamID() {
        Matches match = new Matches();
        int awayTeamID = 20;

        match.setAwayTeamID(awayTeamID);
        assertEquals(awayTeamID, match.getAwayTeamID());
    }

    @Test
    void testGetAndSetGameDate() {
        Matches match = new Matches();
        Date gameDate = new Date();

        match.setGameDate(gameDate);
        assertEquals(gameDate, match.getGameDate(), "The game date should be set and retrieved correctly.");
    }

    @Test
    void testGetAndSetResult() {
        Matches match = new Matches();
        String result = "Home Team";

        match.setResult(result);
        assertEquals(result, match.getResult(), "The result should be set and retrieved correctly.");
    }

    @Test
    void testAllFields() {
        Matches match = new Matches();
        int gameID = 1001;
        int homeTeamID = 10;
        int awayTeamID = 20;
        Date gameDate = new Date();
        String result = "Draw";

        match.setGameID(gameID);
        match.setHomeTeamID(homeTeamID);
        match.setAwayTeamID(awayTeamID);
        match.setGameDate(gameDate);
        match.setResult(result);

        assertEquals(gameID, match.getId());
        assertEquals(homeTeamID, match.getHomeTeamID());
        assertEquals(awayTeamID, match.getAwayTeamID());
        assertEquals(gameDate, match.getGameDate());
        assertEquals(result, match.getResult());
    }
}
