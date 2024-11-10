package com.lms.informationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
@EnableScheduling
public class InformationServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_TEAMS_URL", dotenv.get("DB_TEAMS_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        String dbUrl = System.getProperty("DB_TEAMS_URL");
        System.out.println(dbUrl);

        SpringApplication.run(InformationServiceApplication.class, args);
    }
}
