package com.lms.informationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InformationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InformationServiceApplication.class, args);
    }
}
