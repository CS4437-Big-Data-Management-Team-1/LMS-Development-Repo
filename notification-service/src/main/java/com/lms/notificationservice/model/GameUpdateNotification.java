package com.lms.notificationservice.model;


// Represents a notification for game updates
public class GameUpdateNotification extends Notification {
    public GameUpdateNotification(String recipient) {
        super(recipient);
        setMessage("Hey" + " " + recipient + ",\nThere has been some updates to your tournament!");
        setSubject("Game Update");
    }
}
