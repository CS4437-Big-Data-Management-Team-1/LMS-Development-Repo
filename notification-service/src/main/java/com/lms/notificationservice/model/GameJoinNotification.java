package com.lms.notificationservice.model;


// Represents a notification for creating a game
public class GameJoinNotification extends Notification {
    public GameJoinNotification(String recipient, String gameName, String entryFee) {
        super(recipient);
        setSubject("Game Joined Successfully");
        setMessage("Hey" + " " + recipient + ",\nYou have joined a game!" + "\n\nGame Name: " + gameName + "\nEntry Fee: €" + entryFee);
    }
}
