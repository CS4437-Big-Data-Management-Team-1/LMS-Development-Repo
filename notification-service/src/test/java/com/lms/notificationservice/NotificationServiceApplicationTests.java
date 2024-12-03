package com.lms.notificationservice;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.lms.notificationservice.controller.NotificationController;
import com.lms.notificationservice.model.AccountCreationNotification;
import com.lms.notificationservice.model.GameCreationNotification;
import com.lms.notificationservice.model.GameJoinNotification;
import com.lms.notificationservice.model.GameUpdateNotification;
import com.lms.notificationservice.service.NotificationService;

/**
 * Unit tests for the NotificationController class.
 */
class NotificationServiceApplicationTests {

    @Mock
    private NotificationService notificationService; // Mocked service for sending notifications

    @InjectMocks
    private NotificationController notificationController; // The controller being tested

    /**
     * Initializes the mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    /**
     * Tests sending a valid notification for account creation.
     */
    @Test
    void testSendNotification_ValidAccountCreation() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "account_creation");
        request.put("idToken", "idToken");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(AccountCreationNotification.class));
    }

    /**
     * Tests sending a valid notification for a game update.
     */
    @Test
    void testSendNotification_ValidGameUpdate() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_update");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(GameUpdateNotification.class));
    }

    /**
     * Tests sending a valid notification when a user joins a game.
     */
    @Test 
    void testSendNotification_ValidJoinGame() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_joined");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(GameJoinNotification.class));
    }

    /**
     * Tests sending a valid notification for a new game creation.
     */
    @Test 
    void testSendNotification_ValidGameCreation() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_created");
        request.put("gameName", "Game Name");
        request.put("weeksTillStartDate", "2");
        request.put("entryFee", "10.0");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(GameCreationNotification.class));
    }

    /**
     * Tests the case where the notification type is invalid.
     */
    @Test
    void testSendNotification_InvalidNotificationType() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "invalid_type");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid notification type.", response.getBody());
    }

    /**
     * Tests the case where an exception is thrown by the notification service.
     */
    @Test
    void testSendNotification_ExceptionInService() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_created");

        // Mocking an exception in the notification service
        doThrow(new RuntimeException("Service unavailable")).when(notificationService).sendNotification(any(GameCreationNotification.class));

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to send notification: Service unavailable", response.getBody());
    }
}