package com.lms.notificationservice.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

// Configuration class for RabbitMQ
public class RabbitMQConfig {
    private static final String QUEUE_NAME = "notificationQueue";
    private static final String HOST = "rabbitmq";
    private static final int PORT = 5672;

    public static Channel createChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    public static String getQueueName() {
        return QUEUE_NAME;
    }
}