package com.lms.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().filename("user.env").load();
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_USERS_URL", dotenv.get("DB_USERS_URL"));
        System.setProperty("FIREBASE_API_KEY", dotenv.get("FIREBASE_API_KEY"));


        SpringApplication.run(UserServiceApplication.class, args);
    }
}
