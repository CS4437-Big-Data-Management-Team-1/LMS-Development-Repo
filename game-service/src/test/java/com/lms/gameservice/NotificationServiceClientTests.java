package com.lms.gameservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.lms.gameservice.service.NotificationServiceClient;

class NotificationServiceClientTests {

    @InjectMocks
    private NotificationServiceClient notificationServiceClient;

    @Mock
    private RestTemplate restTemplate;

    private static final String MOCK_NOTIFICATION_URL = "http://notification-service:8085/api/notifications/send";
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

        notificationServiceClient.sendGameCreationNotification(MOCK_USER_EMAIL, "game_created", MOCK_GAME_NAME, "2", "5");

        verify(restTemplate, times(1)).postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class));
        verifyNoMoreInteractions(restTemplate);
    }
    // Test cases for the sendGameJoinedNotification() method
    @Test
    void testSendGameJoinedNotification() {
        String expectedResponse = "Notification sent";
        when(restTemplate.postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        notificationServiceClient.sendGameJoinedNotification(MOCK_USER_EMAIL, "game_joined", MOCK_GAME_NAME, "5");

        verify(restTemplate, times(1)).postForEntity(eq(MOCK_NOTIFICATION_URL), any(), eq(String.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    void testSendGameUpdateNotification_Success() {
        String recipient = "testuser@example.com";
        String type = "game_update";
        String gameName = "Game 1";
        int currentRound = 5;
        String roundStartDate = "2024-11-20";
        String roundEndDate = "2024-11-25";
        String totalPot = "1000";
        String playerStatus = "Active";
        String playerTeamPick = "Team A";

        String notificationUrl = "http://notification-service:8085/api/notifications/send";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.OK);

        // Mock RestTemplate behavior for notifications
        when(restTemplate.postForEntity(eq(notificationUrl), any(), eq(String.class)))
                .thenReturn(mockResponse);

                notificationServiceClient.sendGameUpdateNotification(
                recipient, type, gameName, currentRound, roundStartDate, roundEndDate, totalPot, playerStatus,
                playerTeamPick);

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        verify(restTemplate, times(1)).postForEntity(eq(notificationUrl), captor.capture(), eq(String.class));

        Map<String, String> capturedData = captor.getValue();
        assertEquals(recipient, capturedData.get("recipient"));
        assertEquals(type, capturedData.get("type"));
        assertEquals(gameName, capturedData.get("gameName"));
        assertEquals(String.valueOf(currentRound), capturedData.get("currentRound"));
        assertEquals(roundStartDate, capturedData.get("roundStartDate"));
        assertEquals(roundEndDate, capturedData.get("roundEndDate"));
        assertEquals(totalPot, capturedData.get("totalPot"));
        assertEquals(playerStatus, capturedData.get("playerStatus"));
        assertEquals(playerTeamPick, capturedData.get("playerTeamPick"));
    }

    // Test for handling of game send update notification failure
    @Test
    void testSendGameUpdateNotification_Failure() {
        String recipient = "testuser@example.com";
        String type = "game_update";
        String gameName = "Game 1";
        int currentRound = 5;
        String roundStartDate = "2024-11-20";
        String roundEndDate = "2024-11-25";
        String totalPot = "1000";
        String playerStatus = "Active";
        String playerTeamPick = "Team A";

        String notificationUrl = "http://notification-service:8085/api/notifications/send";

        // Mock RestTemplate to throw an exception
        when(restTemplate.postForEntity(eq(notificationUrl), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Simulated API failure"));

        assertDoesNotThrow(() -> notificationServiceClient.sendGameUpdateNotification(
                recipient, type, gameName, currentRound, roundStartDate, roundEndDate, totalPot, playerStatus,
                playerTeamPick));

        verify(restTemplate, times(1)).postForEntity(eq(notificationUrl), any(), eq(String.class));
    }

    // Test obtaining user email from id
    @Test
    void testGetUserEmailByUid_Success() {
        String uid = "12345";
        String expectedEmail = "testuser@example.com";
        String emailApiUrl = "http://user-service:8080/api/users/" + uid + "/email";

        // Mock RestTemplate behavior for email lookup
        ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedEmail, HttpStatus.OK);
        when(restTemplate.exchange(eq(emailApiUrl), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenReturn(mockResponse);

        String actualEmail = notificationServiceClient.getUserEmailByUid(uid);

        assertEquals(expectedEmail, actualEmail);
        verify(restTemplate, times(1)).exchange(eq(emailApiUrl), eq(HttpMethod.GET), isNull(), eq(String.class));
    }

    // Testing handling of failures when obtaining email by id
    @Test
    void testGetUserEmailByUid_Failure() {
        String uid = "12345";
        String emailApiUrl = "http://user-service:8080/api/users/" + uid + "/email";

        // Mock RestTemplate to simulate an API failure
        when(restTemplate.exchange(eq(emailApiUrl), eq(HttpMethod.GET), isNull(), eq(String.class)))
                .thenThrow(new RuntimeException("Simulated API failure"));

        String actualEmail = notificationServiceClient.getUserEmailByUid(uid);

        assertNull(actualEmail);
        verify(restTemplate, times(1)).exchange(eq(emailApiUrl), eq(HttpMethod.GET), isNull(), eq(String.class));
    }
}
