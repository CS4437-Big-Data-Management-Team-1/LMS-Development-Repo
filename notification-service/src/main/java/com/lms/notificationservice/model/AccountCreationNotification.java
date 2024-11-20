package com.lms.notificationservice.model;

// Represents a notification for account creation
public class AccountCreationNotification extends Notification {
    public AccountCreationNotification(String recipient, String idToken) {
        super(recipient);
        setMessage("Dear" + " " + recipient + ",\nYour account has been created successfully." + "\n\n" + "Your Temporary Login JWT Token is: " + idToken);
        setSubject("Account Creation");
    }
}
