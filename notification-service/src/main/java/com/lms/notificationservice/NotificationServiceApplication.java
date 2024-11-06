package com.lms.notificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import com.lms.notificationservice.controller.NotificationController;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NotificationServiceApplication {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("NOTIFICATION_SERVICE_APP_PASSWORD", dotenv.get("NOTIFICATION_SERVICE_APP_PASSWORD"));
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        NotificationController notificationController = applicationContext.getBean(NotificationController.class);
        System.out.println("Notification Service is up and running");

        // Test run the notification service
        String recipient = "lastmanstanding.notifier@gmail.com";
        String response = notificationController.sendNotification(recipient);
        System.out.println(response);
    }
}
