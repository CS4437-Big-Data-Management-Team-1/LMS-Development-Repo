package com.lms.notificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.lms.notificationservice.model.Notification;
import com.lms.notificationservice.service.NotificationService;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NotificationServiceApplication {
	@Autowired
	private NotificationService notificationService;
	private Notification notification = new Notification("lastmanstanding.notifier@gmail.com", "Hello", "Test Subject");

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("NOTIFICATION_SERVICE_APP_PASSWORD", dotenv.get("NOTIFICATION_SERVICE_APP_PASSWORD"));
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void triggerMail() {
		notificationService.sendNotification(notification);
	}
}
