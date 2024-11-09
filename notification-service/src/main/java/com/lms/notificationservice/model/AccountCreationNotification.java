package com.lms.notificationservice.model;

// Represents a notification for account creation
public class AccountCreationNotification extends Notification {
    public AccountCreationNotification(String recipient) {
        super(recipient);
        setMessage("Dear" + " " + recipient + ",\nYour account has been created successfully.");
        setSubject("Account Creation");
    }
}
