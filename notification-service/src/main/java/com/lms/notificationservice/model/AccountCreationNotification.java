package com.lms.notificationservice.model;

public class AccountCreationNotification extends Notification {
    public AccountCreationNotification(String recipient) {

        super(recipient);
        setMessage("Dear" + " " + recipient + ",\nYour account has been created successfully.");
        setSubject("Account Creation Notification");
    }
}
