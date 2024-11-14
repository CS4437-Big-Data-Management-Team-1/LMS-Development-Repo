package com.lms.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.notificationservice.database.NotificationDatabaseController;
import com.lms.notificationservice.model.Notification;

/**
 * Service class that handles the sending of notifications via email.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * Sends a notification to the specified recipient via email.
     * 
     * @param notification the notification to be sent
     * @throws IllegalArgumentException if the notification or recipient is invalid
     */
    public void sendNotification(Notification notification) {

        // Validate the notification and recipient
        if (notification == null || notification.getRecipient() == null || notification.getRecipient().isEmpty()) {
            logger.error("Notification or recipient email is invalid. Cannot send email.");
            throw new IllegalArgumentException("Invalid notification or recipient email.");
        }

        // Create a new instance of the NotificationDatabaseController
        NotificationDatabaseController db = new NotificationDatabaseController();

        try {
            // Prepare the email message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // Set the sender email address
            message.setTo(notification.getRecipient()); // Set the recipient
            message.setSubject(notification.getSubject()); // Set the subject
            message.setText(notification.getMessage()); // Set the email body text

            // Send the email
            mailSender.send(message);
            logger.info("Email successfully sent to {}", notification.getRecipient());

        } catch (MailException e) {
            // Log and handle any exceptions related to sending the email
            logger.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            logger.error("Unexpected error when sending email to {}: {}", notification.getRecipient(), e.getMessage());
        }

        // Connect to the database and save the notification details
        db.connectToDB();
        db.addEmailToDB(notification);
    }
}