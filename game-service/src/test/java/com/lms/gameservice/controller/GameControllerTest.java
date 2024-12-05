package com.lms.gameservice.controller;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.service.AuthService;
import com.lms.gameservice.service.GameService;
import com.lms.gameservice.service.NotificationServiceClient;
import com.lms.gameservice.gamerequest.GameRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    @Mock
    private AuthService authService;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGame_unauthorized() throws Exception {
        GameRequestDTO gameRequest = new GameRequestDTO("Test Game", BigDecimal.valueOf(50), 2);
        when(authService.validateToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<?> response = gameController.createGame("Bearer invalidToken", gameRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error creating game"));
    }

    @Test
    void getJoinableGames_success() {
        List<Game> joinableGames = new ArrayList<>();
        Game game = new Game();
        game.setName("Joinable Game");
        joinableGames.add(game);

        when(gameService.getJoinableGames()).thenReturn(joinableGames);

        ResponseEntity<List<Game>> response = gameController.getJoinableGames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(joinableGames, response.getBody());
        verify(gameService, times(1)).getJoinableGames();
    }

    @Test
    void joinGame_unauthorized() throws Exception {
        int gameId = 1;
        when(authService.validateToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<String> response = gameController.joinGame("Bearer invalidToken", gameId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Error joining game"));
    }
}
