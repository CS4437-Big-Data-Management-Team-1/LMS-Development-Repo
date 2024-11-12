package com.lms.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Main entry point for the Payment Service Application.
 *
 * This class initializes the application, loads environment variables,
 * and configures system properties necessary for the application to function.
 *
 * @author Callum Carroll
 */

@SpringBootApplication
public class PaymentServiceApplication {

    /**
     * This method loads environment variables using the Dotenv library, setting the
     * Stripe API key as a system property. It then starts the Spring application context.
     *
     * @param args command-line arguments passed to the application
     */

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_USERS_URL", dotenv.get("DB_USERS_URL"));

        SpringApplication.run(PaymentServiceApplication.class, args);


    }
}