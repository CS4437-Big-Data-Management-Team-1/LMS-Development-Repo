package com.lms.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.notificationservice.model.Notification;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Handles sending of notifications
    public void sendNotification(Notification notification) {
        if (notification == null || notification.getRecipient() == null || notification.getRecipient().isEmpty()) {
            logger.error("Notification or recipient email is invalid. Cannot send email.");
            throw new IllegalArgumentException("Invalid notification or recipient email.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); 
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getMessage());

            mailSender.send(message);
            logger.info("Email successfully sent to {}", notification.getRecipient());

        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error when sending email to {}: {}", notification.getRecipient(), e.getMessage());
        }
    }
}
