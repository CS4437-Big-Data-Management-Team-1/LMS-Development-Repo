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
        String weeksTillStartDate = request.get("weeksTillStartDate");
        String entryFee = request.get("entryFee");
        String currentRound = request.get("currentRound");
        String roundStartDate = request.get("roundStartDate");
        String roundEndDate = request.get("roundEndDate");
        String totalPot = request.get("totalPot");
        String playerStatus = request.get("playerStatus");
        String playerTeamPick = request.get("playerTeamPick");

        // Create the appropriate notification object based on the type
        Notification notification = createNotification(type, recipient, idToken, gameName, weeksTillStartDate, entryFee, currentRound, roundStartDate, roundEndDate, totalPot, playerStatus, playerTeamPick);

        // Validate invalid notification type
        if (notification == null) {
            return ResponseEntity.badRequest().body("Invalid notification type.");
        }

        // Try send the notification
        try {
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
    private Notification createNotification(String type, String recipient, String idToken, String gameName, String weeksTillStartDate, String entryFee, String currentRound, String roundStartDate, String roundEndDate, String totalPot, String playerStatus, String playerTeamPick) {
        return switch (type != null ? type.toLowerCase() : "") {
            case "account_creation" -> new AccountCreationNotification(recipient, idToken);
            case "game_update" -> new GameUpdateNotification(recipient, gameName, currentRound, roundStartDate, roundEndDate, totalPot, playerStatus, playerTeamPick);
            case "game_created" -> new GameCreationNotification(recipient, gameName, weeksTillStartDate, entryFee);
            case "game_joined" -> new GameJoinNotification(recipient, gameName, entryFee);
            default -> null;
        };
    }
}