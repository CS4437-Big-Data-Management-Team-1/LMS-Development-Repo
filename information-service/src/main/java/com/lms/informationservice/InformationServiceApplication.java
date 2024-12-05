package com.lms.informationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
@EnableScheduling
public class InformationServiceApplication {

    public static void main(String[] args) {
        String dotDEV =  System.getenv("USE_DOTENV");

//        if ( "true".equalsIgnoreCase(dotDEV)){
//            try{
//                io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
//                dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
//            } catch (Exception e){
//                System.err.println("Dotenv could not load environment variables:" + e.getMessage());
//            }
//        }

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_TEAMS_URL", dotenv.get("DB_TEAMS_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));


        SpringApplication.run(InformationServiceApplication.class, args);
    }
}
