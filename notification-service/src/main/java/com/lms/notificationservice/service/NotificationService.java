package com.lms.notificationservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.notificationservice.model.Notification;

@Service
public class NotificationService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(Notification notification) {
        rabbitTemplate.convertAndSend("notificationQueue", notification);
        
        sendEmail(notification);
    }

    private void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        mailSender.send(message);
    }
}

