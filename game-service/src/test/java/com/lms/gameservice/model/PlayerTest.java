package com.lms.gameservice.model;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerGettersAndSetters() {
        Player player = new Player();
        player.setId(1L);
        player.setUserId("user123");
        player.setActive(true);
        player.setTeamPick("Team A");
        player.setNextPick("Team B");

        assertEquals(1L, player.getId());
        assertEquals("user123", player.getUserId());
        assertTrue(player.isActive());
        assertEquals("Team A", player.getTeamPick());
        assertEquals("Team B", player.getNextPick());
    }

    @Test
    void testTeamsAvailableSerialization() throws JsonProcessingException {
        Player player = new Player();
        List<String> availableTeams = new ArrayList<>(List.of("Team A", "Team B"));
        player.setTeamsAvailable(new ArrayList<>(availableTeams));

        List<String> retrievedTeams = player.getTeamsAvailable();
        assertEquals(2, retrievedTeams.size());
        assertTrue(retrievedTeams.contains("Team A"));
        assertTrue(retrievedTeams.contains("Team B"));
    }

    @Test
    void testTeamsUsedSerialization() throws JsonProcessingException {
        Player player = new Player();
        List<String> usedTeams = new ArrayList<>(List.of("Team C", "Team D"));
        player.setTeamsUsed(new ArrayList<>(usedTeams));

        List<String> retrievedTeams = player.getTeamsUsed();
        assertEquals(2, retrievedTeams.size());
        assertTrue(retrievedTeams.contains("Team C"));
        assertTrue(retrievedTeams.contains("Team D"));
    }
}
