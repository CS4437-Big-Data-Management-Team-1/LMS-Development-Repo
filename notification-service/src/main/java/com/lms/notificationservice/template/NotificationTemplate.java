package com.lms.notificationservice.template;

public class NotificationTemplate {
    public static String getWelcomeTemplate(String userName) {
        return "Welcome, " + userName + "! We're glad to have you.";
    }

    public static String getGoodbyeTemplate(String userName) {
        return "Goodbye, " + userName + "! We'll miss you.";
    }
}
