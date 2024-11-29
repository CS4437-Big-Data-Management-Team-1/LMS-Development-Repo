package com.lms.gameservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GameServiceApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().filename("game.env").load();
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_GAMES_URL", dotenv.get("DB_GAMES_URL"));

        SpringApplication.run(GameServiceApplication.class, args);
    }
}