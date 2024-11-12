package com.lms.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("NOTIFICATION_SERVICE_APP_PASSWORD", dotenv.get("NOTIFICATION_SERVICE_APP_PASSWORD"));
        System.setProperty("DB_GAMES_URL", dotenv.get("DB_GAMES_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    // Notify when the application is ready to accept requests
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("Notification Service is up and running and ready to accept requests.");
    }
}
