package com.lms.notificationservice.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.notificationservice.config.RabbitMQConfig;
import com.lms.notificationservice.model.Notification;
import com.rabbitmq.client.Channel;

// Service class that handles sending notifications to RabbitMQ for email delivery
@Service
public class NotificationService {
    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    // ObjectMapper for converting objects to JSON
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Sender email address for sending emails
    private final String senderEmail = "lastmanstanding.notifier@gmail.com";
    // Send email to RabbitMQ
    public void sendNotification(Notification notification) {
        if (notification == null || notification.getRecipient().isEmpty()) {
            logger.error("Invalid notification or recipient email.");
            throw new IllegalArgumentException("Invalid notification or recipient email.");
        }
        // Try to send the email
        try {
            // Create a map to hold the email data
            Map<String, String> emailData = new HashMap<>();
            emailData.put("from", senderEmail);
            emailData.put("to", notification.getRecipient());
            emailData.put("subject", notification.getSubject());
            emailData.put("message", notification.getMessage());

            // Convert the map to JSON
            String messageJson = objectMapper.writeValueAsString(emailData);

            // Send the message to RabbitMQ
            sendNotificationToQueue(messageJson);

        } catch (MailException e) {
            // Log and handle any exceptions related to sending the email
            logger.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            logger.error("Unexpected error when sending email to {}: {}", notification.getRecipient(), e.getMessage());
        }
    }

    // Send the notification message to RabbitMQ
    public void sendNotificationToQueue(String messageJson) {
        try {
            Channel channel = RabbitMQConfig.createChannel();
            channel.queueDeclare(RabbitMQConfig.getQueueName(), false, false, false, null);
            channel.basicPublish("", RabbitMQConfig.getQueueName(), null, messageJson.getBytes());
            channel.close();
        } catch (Exception e) {
            logger.error("Failed to send notification to RabbitMQ: {}", e.getMessage());
        }
    }
}
