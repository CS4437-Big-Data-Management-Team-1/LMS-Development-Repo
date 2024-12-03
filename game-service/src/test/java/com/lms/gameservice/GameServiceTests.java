package com.lms.gameservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.lms.gameservice.controller.GameController;

class GameServiceTests {

    @InjectMocks
    private GameController gameController;

    @Mock
    private RestTemplate restTemplate;

    private static final String MOCK_NOTIFICATION_URL = "http://localhost:8085/api/notifications/send";
    private static final String MOCK_GAME_NAME = "Test Game";
    private static final double MOCK_ENTRY_FEE = 100.0;
    private static final String MOCK_USER_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for the sendGameCreationNotification() method
    @Test
    void testSendGameCreationNotification() {
        String expectedResponse = "Notification sent";
        when(restTemplate.postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        gameController.sendGameCreationNotification(MOCK_USER_EMAIL, "game_created", MOCK_GAME_NAME, "2", "5");

        verify(restTemplate, times(1)).postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class));
        verifyNoMoreInteractions(restTemplate);
    }
    // Test cases for the sendGameJoinedNotification() method
    @Test
    void testSendGameJoinedNotification() {
        String expectedResponse = "Notification sent";
        when(restTemplate.postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        gameController.sendGameJoinedNotification(MOCK_USER_EMAIL, "game_joined", MOCK_GAME_NAME, "5");

        verify(restTemplate, times(1)).postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class));
        verifyNoMoreInteractions(restTemplate);
    }
}
