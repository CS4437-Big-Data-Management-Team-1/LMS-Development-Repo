package com.lms.notificationservice.model;

// Represents a notification for game updates
public class GameUpdateNotification extends Notification {

    public GameUpdateNotification(
            String recipient,
            String gameName,
            String currentRound,
            String roundStartDate,
            String roundEndDate,
            String totalPot,
            String playerStatus,
            String playerTeamPick) {

        super(recipient);

        // Construct our email dynamically
        StringBuilder message = new StringBuilder();
        message.append("Hey ").append(recipient).append(",\n\n")
               .append("There have been updates to your tournament!\n")
               .append("Here are the details:\n\n")
               .append("Game Name: ").append(gameName).append("\n")
               .append("Current Round: ").append(currentRound).append("\n")
               .append("Round Start Date: ").append(roundStartDate).append("\n")
               .append("Round End Date: ").append(roundEndDate).append("\n")
               .append("Total Prize Pool: ").append(totalPot).append("\n")
               .append("Your Status: ").append(playerStatus).append("\n")
               .append("Your Team Pick: ").append(playerTeamPick).append("\n\n")
               .append("Check back for more updates soon!\n\n")
               .append("Best regards,\n")
               .append("Last Man Standing");

        // Set the subject and message for the notification
        setSubject("Game Update Notification: " + gameName);
        setMessage(message.toString());
    }
}
