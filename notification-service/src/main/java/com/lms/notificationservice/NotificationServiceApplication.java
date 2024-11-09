package com.lms.notificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import com.lms.notificationservice.controller.NotificationController;

import io.github.cdimascio.dotenv.Dotenv;
import com.lms.notificationservice.database.NotificationDatabaseController;

@SpringBootApplication
public class NotificationServiceApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private NotificationController notificationController;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("../").load();
        System.setProperty("NOTIFICATION_SERVICE_APP_PASSWORD", dotenv.get("NOTIFICATION_SERVICE_APP_PASSWORD"));
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
