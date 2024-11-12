package com.lms.notificationservice.model;


// Represents a notification for creating a game
public class GameJoinedNotification extends Notification {
    public GameJoinedNotification(String recipient) {
        super(recipient);
        setMessage("Hey" + " " + recipient + ",\nYou have joined a game!");
        setSubject("Game Joined Successfully");
    }
}
