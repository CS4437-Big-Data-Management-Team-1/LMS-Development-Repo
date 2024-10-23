package com.lms.notificationservice.retry;


import com.lms.notificationservice.model.Notification;

public class NotificationRetryHandler {
    public void handleRetry(Notification notification) {
        System.out.println("Retrying to send notification: " + notification.getMessage());
    }
}