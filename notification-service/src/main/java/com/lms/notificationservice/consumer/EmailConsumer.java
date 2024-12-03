package com.lms.notificationservice.consumer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.notificationservice.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

// Consumer class that listens for messages on the RabbitMQ queue and sends emails
public class EmailConsumer {
    // Queue name for the RabbitMQ queue
    private static final String QUEUE_NAME = "notificationQueue";
    // JavaMailSender for sending emails
    private final JavaMailSender mailSender;
    // ObjectMapper for deserializing JSON
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    // Constructor that initializes the JavaMailSender
    public EmailConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Start listening for messages on the RabbitMQ queue
    public void startListening() throws Exception {
        Channel channel = RabbitMQConfig.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageJson = new String(delivery.getBody(), "UTF-8");
            logger.info("Received message: {}", messageJson); // Log the received message
            try {
                // Deserialize the message as a map
                Map<String, String> emailData = objectMapper.readValue(messageJson, Map.class);

                // Extract data from the map
                String from = emailData.get("from");
                String to = emailData.get("to");
                String subject = emailData.get("subject");
                String messageText = emailData.get("message");

                // Create the message object
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(messageText);

                // Send the email
                mailSender.send(message);
                logger.info("Email successfully sent to {}", to);
            } catch (Exception e) {
                logger.error("Failed to send email: {}", e.getMessage());
            }
        };
        // Start consuming messages
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
