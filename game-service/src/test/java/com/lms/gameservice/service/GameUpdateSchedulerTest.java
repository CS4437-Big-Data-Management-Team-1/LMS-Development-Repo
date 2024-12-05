package com.lms.gameservice.service;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;
import com.lms.gameservice.repository.GameRepository;
import com.lms.gameservice.repository.ResultsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class GameUpdateSchedulerTest {

    @InjectMocks
    private GameUpdateScheduler scheduler;

    @Mock
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ResultsRepository resultsRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @Mock
    private InformationServiceClient informationServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateActiveGames_success() {
        Game activeGame = new Game();
        activeGame.setId(1);
        activeGame.setName("Active Game");
        activeGame.setStatus("ACTIVE");
        activeGame.setCurrentRound(1);
        activeGame.setCurrentRoundStartDate(LocalDateTime.now().minusDays(1));
        activeGame.setCurrentRoundEndDate(LocalDateTime.now().plusDays(100));

        List<Game> activeGames = List.of(activeGame);
        when(gameRepository.findByStatus("ACTIVE")).thenReturn(activeGames);
        List<Player> activePlayers = new ArrayList<>();
        Player player = new Player();
        player.setUserId("user123");
        activePlayers.add(player);

        when(gameService.getActivePlayersInGame(anyInt())).thenReturn(activePlayers);
        when(notificationServiceClient.getUserEmailByUid("user123")).thenReturn("user@example.com");

        scheduler.updateActiveGames();

        verify(gameRepository, times(1)).findByStatus("ACTIVE");
        verify(gameService, times(1)).nextRound(anyInt());
        verify(notificationServiceClient, times(1)).sendGameUpdateNotification(
                eq("user@example.com"), any(), any(), anyInt(), any(), any(), any(), any(), any());
    }

    @Test
    void checkFutureGames_success() {
        Game createdGame = new Game();
        createdGame.setId(2);
        createdGame.setName("Future Game");
        createdGame.setStatus("CREATED");
        createdGame.setStartDate(java.time.LocalDateTime.now().minusDays(1)); 

        List<Game> futureGames = List.of(createdGame);
        when(gameRepository.findByStatus("CREATED")).thenReturn(futureGames);

        scheduler.checkFutureGames();

        verify(gameRepository, times(1)).findByStatus("CREATED");
        verify(gameService, times(1)).startGame(createdGame.getId());
    }

    @Test
    void uploadResults_success() {
        List<com.lms.gameservice.matches.MatchesDTO> matches = new ArrayList<>();
        com.lms.gameservice.matches.MatchesDTO match = new com.lms.gameservice.matches.MatchesDTO();
        match.setResult("Team A");
        matches.add(match);

        when(informationServiceClient.fetchMatchesWithinDateRange("2024-10-10", "2024-10-17")).thenReturn(matches);
        when(resultsRepository.save(any())).thenReturn(null);  

        scheduler.uploadResults();

        verify(resultsRepository, times(1)).save(any());
    }

    @Test
    void updateActiveGames_noGames() {
        when(gameRepository.findByStatus("ACTIVE")).thenReturn(new ArrayList<>());

        scheduler.updateActiveGames();

        verify(gameRepository, times(1)).findByStatus("ACTIVE");
        verify(gameService, times(0)).nextRound(anyInt());
    }
}
