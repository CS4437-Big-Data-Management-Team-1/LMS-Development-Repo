package com.lms.notificationservice.model;


// Represents a notification for joining a game
public class GameCreationNotification extends Notification {
    public GameCreationNotification(String recipient, String gameName, String weeksTillStartDate, String entryFee) { 
        super(recipient);
        setSubject("Game Created Successfully");
        setMessage("Hey" + " " + recipient + ",\nYou have created a game!" + "\n\nGame Name: " + gameName + "\nWeeks Till Start Date: " + weeksTillStartDate + "\nEntry Fee: €" + entryFee);
    }
}
