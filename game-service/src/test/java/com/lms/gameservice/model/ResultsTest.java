package com.lms.gameservice.model;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ResultsTest {

    @Test
    void testResultsGettersAndSetters() {
        Results results = new Results();
        results.setWinners(new ArrayList<>(List.of("Team A", "Team B")));

        List<String> winners = results.getWinners();
        assertEquals(2, winners.size());
        assertTrue(winners.contains("Team A"));
        assertTrue(winners.contains("Team B"));
    }

    @Test
    void testWinnersSerialization() throws JsonProcessingException {
        Results results = new Results();
        ArrayList<String> winners = new ArrayList<>(List.of("Team X", "Team Y"));
        results.setWinners(winners);

        List<String> retrievedWinners = results.getWinners();
        assertEquals(2, retrievedWinners.size());
        assertTrue(retrievedWinners.contains("Team X"));
        assertTrue(retrievedWinners.contains("Team Y"));
    }
}
