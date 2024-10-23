package com.lms.notificationservice.controller;

import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifications")
    public void sendNotification(@RequestBody Notification notification) {
        notificationService.sendNotification(notification);
    }
}
