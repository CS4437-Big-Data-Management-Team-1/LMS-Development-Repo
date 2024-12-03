package com.lms.notificationservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.lms.notificationservice.consumer.EmailConsumer;

// Configuration class for the application
@Configuration
public class AppConfig {

    private final JavaMailSender mailSender;

    public AppConfig(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    // Start the email consumer when the application starts
    @Bean
    public CommandLineRunner startEmailConsumer() {
        return args -> {
            EmailConsumer emailConsumer = new EmailConsumer(mailSender);
            emailConsumer.startListening();
        };
    }
}