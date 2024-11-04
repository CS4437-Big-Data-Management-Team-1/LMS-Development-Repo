package com.lms.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.notificationservice.model.Notification;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("lastmanstanding.notifier@gmail.com");
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        mailSender.send(message);

        System.out.println("Email sent to " + notification.getRecipient());
    }
}