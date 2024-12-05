package com.lms.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * The main entry point for the Notification Service application.
 * This class is responsible for loading environment variables and
 * starting the Spring Boot application.
 */
@SpringBootApplication
public class NotificationServiceApplication {
    /**
     * Load our environment variables
     * Run the Spring Boot application
     */
    public static void main(String[] args) {
        String dotDEV =  System.getenv("USE_DOTENV");

        if ( "true".equalsIgnoreCase(dotDEV)){
            try{
                io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
                dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
            } catch (Exception e){
                System.err.println("Dotenv could not load environment variables:" + e.getMessage());
            }
        }
        Dotenv dotenv = Dotenv.load();
        System.setProperty("NOTIFICATION_SERVICE_APP_PASSWORD", dotenv.get("NOTIFICATION_SERVICE_APP_PASSWORD"));
        System.setProperty("DB_GAMES_URL", dotenv.get("DB_GAMES_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    /**
     * Let the user know when the application is ready to accept requests.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("Notification Service is up and running and ready to accept requests.");
    }
}