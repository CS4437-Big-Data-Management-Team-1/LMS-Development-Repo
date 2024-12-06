package com.lms.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) throws IOException {

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
        setSystemProperty("DB_USERNAME");
        setSystemProperty("DB_PASSWORD");
        setSystemProperty("DB_USERS_URL");
        setSystemProperty("FIREBASE_API_KEY");

//        String firebaseSetupBase64 = System.getenv("FIREBASE_SETUP");
//        byte[] decodedBytes = Base64.getDecoder().decode(firebaseSetupBase64);
//        String filePath = "user-service/src/main/resources/firebase-setup.json";
//        Files.write(Paths.get(filePath), decodedBytes);

        SpringApplication.run(UserServiceApplication.class, args);
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