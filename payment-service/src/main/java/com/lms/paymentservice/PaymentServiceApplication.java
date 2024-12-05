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
     * Stripe API key as a system property, along with the DB connectors.
     * It then starts the Spring application context.
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
        System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_USERS_URL", dotenv.get("DB_USERS_URL"));

        SpringApplication.run(PaymentServiceApplication.class, args);


    }
}