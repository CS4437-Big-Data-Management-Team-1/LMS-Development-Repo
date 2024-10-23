package com.lms.notificationservice.repository;

import java.util.ArrayList;
import java.util.List;

import com.lms.notificationservice.model.Notification;

public class NotificationRepository {
    private List<Notification> notificationLog = new ArrayList<>();

    public void save(Notification notification) {
        notificationLog.add(notification);
        System.out.println("Notification logged: " + notification.getMessage());
    }

    public List<Notification> findAll() {
        return notificationLog;
    }
}
