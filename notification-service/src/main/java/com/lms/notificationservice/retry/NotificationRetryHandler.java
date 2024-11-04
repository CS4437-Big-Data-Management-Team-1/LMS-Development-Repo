package com.lms.notificationservice.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;

@Component
public class NotificationRetryHandler {

    @Autowired
    private NotificationService notificationService;

    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    public void sendWithRetry(Notification notification) {
        System.out.println("Attempting to send notification with retry...");
        notificationService.sendNotification(notification);
    }

    @Recover
    public void recover(Exception e, Notification notification) {
        System.out.println("Failed to send notification after retries. Reason: " + e.getMessage());
    }
}
