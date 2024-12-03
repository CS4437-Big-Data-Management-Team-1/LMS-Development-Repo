package com.lms.gameservice.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lms.gameservice.controller.GameController;

@Service
public class NotificationServiceClient {

    private final RestTemplate restTemplate;
    private static final Logger logger = LogManager.getLogger(GameController.class);

    @Autowired
    public NotificationServiceClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }


    public void sendGameUpdateNotification(
        String recipient,
        String type,
        String gameName,
        int currentRound,
        String roundStartDate,
        String roundEndDate,
        String totalPot,
        String playerStatus,
        String playerTeamPick
    ) {
        String notificationUrl = "http://localhost:8085/api/notifications/send";

        // Build the notification data as a map of individual fields
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("recipient", recipient);
        notificationData.put("type", type);
        notificationData.put("gameName", gameName);
        notificationData.put("currentRound", String.valueOf(currentRound));
        notificationData.put("roundStartDate", roundStartDate);
        notificationData.put("roundEndDate", roundEndDate);
        notificationData.put("totalPot", totalPot);
        notificationData.put("playerStatus", playerStatus);
        notificationData.put("playerTeamPick", playerTeamPick);

        try {
            restTemplate.postForEntity(notificationUrl, notificationData, String.class);
            logger.info("Notification sent to {} for game {} (type: {})", recipient, gameName, type);
        } catch (Exception e) {
            logger.error("Failed to send notification to {} for game {}. Error: {}", recipient, gameName, e.getMessage());
        }
    }

    

    public String getUserEmailByUid(String uid) {
        // Call the UserController's endpoint to get the email
        String url = "http://localhost:8080/api/users/" + uid + "/email";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody(); // Return the email
            } else {
                return null; // Handle the case when the user is not found
            }
        } catch (Exception e) {
            // Handle errors
            return null;
        }
    }

}
