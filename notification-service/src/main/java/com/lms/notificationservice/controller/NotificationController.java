package com.lms.notificationservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.notificationservice.model.AccountCreationNotification;
import com.lms.notificationservice.model.GameCreationNotification;
import com.lms.notificationservice.model.GameJoinNotification;
import com.lms.notificationservice.model.GameUpdateNotification;
import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;

/**
 * Controller class that handles notification-related operations.
 * Provides an endpoint to send notifications based on user request.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Handles the request to send a notification to a recipient.
     * Validates the request for required fields and correct types, 
     * then delegates the notification sending to the notification service.
     * 
     * @param request the notification request body containing "recipient" and "type" keys
     * @return ResponseEntity with status and message about the notification request
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> request) {
        String recipient = request.get("recipient");
        String type = request.get("type");
        String idToken = request.get("idToken");
        String gameName = request.get("gameName");
        int weeksTillStartDate;
        try {
            weeksTillStartDate = request.get("weeksTillStartDate") != null 
                ? Integer.parseInt(request.get("weeksTillStartDate")) 
                : 0;
        } catch (NumberFormatException e) {
            weeksTillStartDate = 0; // Default if parsing fails
        }
    
        double entryFee;
        try {
            entryFee = request.get("entryFee") != null 
                ? Double.parseDouble(request.get("entryFee")) 
                : 0.0;
        } catch (NumberFormatException e) {
            entryFee = 0.0; // Default if parsing fails
        }

        if (recipient == null || recipient.isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient email is required.");
        }

        // Validate missing recipient
        if (recipient == null || recipient.isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient email is required.");
        }

        // Create the appropriate notification object based on the type
        Notification notification = createNotification(type, recipient, idToken, gameName, weeksTillStartDate, entryFee);

        // Validate invalid notification type
        if (notification == null) {
            return ResponseEntity.badRequest().body("Invalid notification type.");
        }

        try {
            // Send the notification
            notificationService.sendNotification(notification);
            return ResponseEntity.ok("Notification sent to " + recipient);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }

    /**
     * Creates the corresponding notification object based on the provided type.
     * 
     * @param type the notification type
     * @param recipient the recipient's email address
     * @return the corresponding Notification object or null if the type is invalid
     */
    private Notification createNotification(String type, String recipient, String idToken, String gameName, int weeksTillStartDate, double entryFee) {
        switch (type != null ? type.toLowerCase() : "") {
            case "account_creation":
                return new AccountCreationNotification(recipient, idToken);
            case "game_update":
                return new GameUpdateNotification(recipient);
            case "game_created":
                return new GameCreationNotification(recipient, gameName, weeksTillStartDate, entryFee);
            case "game_joined":
                return new GameJoinNotification(recipient, gameName, entryFee);
            default:
                return null;
        }
    }
}