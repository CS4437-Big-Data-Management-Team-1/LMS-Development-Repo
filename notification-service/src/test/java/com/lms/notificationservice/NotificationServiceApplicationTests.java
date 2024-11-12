package com.lms.notificationservice;

import com.lms.notificationservice.model.*;
import com.lms.notificationservice.service.NotificationService;
import com.lms.notificationservice.controller.NotificationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification_ValidAccountCreation() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "account_creation");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(AccountCreationNotification.class));
    }

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

    @Test 
    void testSendNotification_ValidGameCreation() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_created");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Notification sent to user@example.com", response.getBody());
        verify(notificationService).sendNotification(any(GameCreationNotification.class));
    }

    @Test
    void testSendNotification_MissingRecipient() {
        Map<String, String> request = new HashMap<>();
        request.put("type", "account_creation");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Recipient email is required.", response.getBody());
    }

    @Test
    void testSendNotification_InvalidNotificationType() {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "invalid_type");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid notification type.", response.getBody());
    }

    @Test
    void testSendNotification_ExceptionInService() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("recipient", "user@example.com");
        request.put("type", "game_created");

        doThrow(new RuntimeException("Service unavailable")).when(notificationService).sendNotification(any(GameCreationNotification.class));

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to send notification: Service unavailable", response.getBody());
    }
}
