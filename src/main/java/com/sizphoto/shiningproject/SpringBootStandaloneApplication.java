package com.sizphoto.shiningproject;

import com.sizphoto.shiningproject.engine.GameEngine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootStandaloneApplication implements CommandLineRunner {

    private GameEngine gameEngine;

    public SpringBootStandaloneApplication(final GameEngine gameEngine){
        this.gameEngine = gameEngine;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStandaloneApplication.class, args);
    }

    @Override
    public void run(String... args) {
        gameEngine.run();
    }
}