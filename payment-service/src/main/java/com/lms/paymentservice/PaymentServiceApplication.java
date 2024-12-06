package com.lms.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the Payment Service Application.
 *
 * This class initializes the application, loads environment variables,
 * and configures system properties necessary for the application to function.
 *
 * @author Callum Carroll
 */

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

    /**
     * This method loads environment variables using the Dotenv library, setting the
     * Stripe API key as a system property, along with the DB connectors.
     * It then starts the Spring application context.
     */

    public static void main(String[] args) {
        String useDotenv = System.getenv("USE_DOTENV"); // Check the flag

        if ("true".equalsIgnoreCase(useDotenv)) {
            try {
                // Load environment variables from .env using dotenv
                Dotenv dotenv = Dotenv.load();
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                    System.out.println("Loaded: " + entry.getKey());
                });
            } catch (Exception e) {
                System.err.println("Dotenv could not load environment variables: " + e.getMessage());
            }
        } else {
            System.out.println("USE_DOTENV is false or not set. Using system environment variables.");
        }
        setSystemProperty("STRIPE_SECRET_KEY");
        setSystemProperty("DB_USERNAME");
        setSystemProperty("DB_PASSWORD");
        setSystemProperty("DB_USERS_URL");

        SpringApplication.run(PaymentServiceApplication.class, args);


    }

    private static void setSystemProperty(String key) {
        String value = System.getenv(key); // For local development
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.err.println("WARNING: Environment variable " + key + " is not set.");
        }
    }
}