package com.lms.notificationservice.model;


// Represents a notification for joining a game
public class GameCreationNotification extends Notification {
    public GameCreationNotification(String recipient) {
        super(recipient);
        setMessage("Hey" + " " + recipient + ",\nYou have created a game!");
        setSubject("Game Created Successfully");
    }
}
