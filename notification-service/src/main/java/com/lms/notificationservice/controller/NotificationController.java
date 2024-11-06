package com.lms.notificationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.notificationservice.model.AccountCreationNotification;
import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public String sendNotification(@RequestParam String recipient) {
        Notification notification = new AccountCreationNotification(recipient);
        notificationService.sendNotification(notification);
        return "Notification sent to " + recipient;
    }
}
