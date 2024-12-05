package com.lms.gameservice.service;

import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pickTeam_success() {
        Player player = new Player();
        List<String> availableTeams = new ArrayList<>(List.of("Team A", "Team B"));
        player.setTeamsAvailable(new ArrayList<>(availableTeams));
        player.setTeamsUsed(new ArrayList<>());

        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.pickTeam(player, "Team A");

        assertFalse(player.getTeamsAvailable().contains("Team A"));
        assertTrue(player.getTeamsUsed().contains("Team A"));
        assertEquals("Team A", player.getNextPick());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void pickTeam_failsWhenTeamNotAvailable() {
        Player player = new Player();
        player.setTeamsAvailable(new ArrayList<>(List.of("Team A")));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.pickTeam(player, "Team C");
        });
        assertEquals("Team not available for pick.", exception.getMessage());
    }

    @Test
    void changeTeamPick_success() {
        Player player = new Player();
        player.setNextPick("Team A");
        player.setTeamsAvailable(new ArrayList<>(List.of("Team B")));
        player.setTeamsUsed(new ArrayList<>(List.of("Team A")));

        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.changeTeamPick(player, "Team B");

        assertTrue(player.getTeamsUsed().contains("Team B"));
        assertFalse(player.getTeamsUsed().contains("Team A"));
        assertTrue(player.getTeamsAvailable().contains("Team A"));
        assertFalse(player.getTeamsAvailable().contains("Team B"));
        assertEquals("Team B", player.getNextPick());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void changeTeamPick_failsWhenOldTeamNotUsed() {
        Player player = new Player();
        player.setNextPick("Team A");
        player.setTeamsUsed(new ArrayList<>(List.of("Team B")));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.changeTeamPick(player, "Team C");
        });
        assertEquals("Team not found in used teams.", exception.getMessage());
    }

    @Test
    void autoPickTeam_success() {
        Player player = new Player();
        player.setTeamsAvailable(new ArrayList<>(List.of("Team A", "Team B")));
        player.setTeamsUsed(new ArrayList<>());

        when(playerRepository.save(any(Player.class))).thenReturn(player);

        playerService.autoPickTeam(player);

        assertFalse(player.getTeamsAvailable().contains("Team A"));
        assertTrue(player.getTeamsUsed().contains("Team A"));
        assertEquals("Team A", player.getTeamPick());
        verify(playerRepository, times(1)).save(player);
    }

    @Test
    void getPlayerByGameIdAndUserId_success() {
        Player player = new Player();
        when(playerRepository.findByGameIdAndUserId(1, "user123")).thenReturn(player);

        Player foundPlayer = playerService.getPlayerByGameIdAndUserId(1, "user123");

        assertNotNull(foundPlayer);
        verify(playerRepository, times(1)).findByGameIdAndUserId(1, "user123");
    }

    @Test
    void getPlayerByGameIdAndUserId_notFound() {
        when(playerRepository.findByGameIdAndUserId(1, "user123")).thenReturn(null);

        Player foundPlayer = playerService.getPlayerByGameIdAndUserId(1, "user123");

        assertNull(foundPlayer);
        verify(playerRepository, times(1)).findByGameIdAndUserId(1, "user123");
    }
}
