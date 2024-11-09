package com.lms.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.database.NotificationDatabaseController;
@Service
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(Notification notification) {
        NotificationDatabaseController db = new NotificationDatabaseController();
        SimpleMailMessage message = new SimpleMailMessage();
        db.connectToDB();
        db.addEmailToDB(notification);
        message.setFrom("lastmanstanding.notifier@gmail.com");
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        mailSender.send(message);

        System.out.println("Email sent to " + notification.getRecipient());
    }
}