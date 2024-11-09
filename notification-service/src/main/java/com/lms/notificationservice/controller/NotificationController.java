package com.lms.notificationservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.notificationservice.model.AccountCreationNotification;
import com.lms.notificationservice.model.GameUpdateNotification;
import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Send a notification to a recipient at endpoint - notifications/send
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> request) {
        String recipient = request.get("recipient");
        String type = request.get("type");

        if (recipient == null || recipient.isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient email is required.");
        }

        Notification notification = createNotification(type, recipient);

        if (notification == null) {
            return ResponseEntity.badRequest().body("Invalid notification type.");
        }

        try {
            notificationService.sendNotification(notification);
            return ResponseEntity.ok("Notification sent to " + recipient);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }

    // Handle the type of notification to be sent
    private Notification createNotification(String type, String recipient) {
        switch (type != null ? type.toLowerCase() : "") {
            case "account_creation":
                return new AccountCreationNotification(recipient);
            case "game_update":
                return new GameUpdateNotification(recipient);
            default:
                return null;
        }
    }
}
