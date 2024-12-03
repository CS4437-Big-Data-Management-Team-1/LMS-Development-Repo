package com.lms.notificationservice.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

// Configuration class for RabbitMQ
public class RabbitMQConfig {
    private static final String QUEUE_NAME = "notificationQueue";
    private static final String HOST = "localhost";

    public static Channel createChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    public static String getQueueName() {
        return QUEUE_NAME;
    }
}