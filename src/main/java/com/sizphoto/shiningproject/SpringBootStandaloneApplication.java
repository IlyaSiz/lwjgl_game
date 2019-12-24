package com.sizphoto.shiningproject;

import com.sizphoto.shiningproject.engine.GameEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootStandaloneApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootStandaloneApplication.class);

    private GameEngine gameEngine;

    @Autowired
    public SpringBootStandaloneApplication(final GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SpringBootStandaloneApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        try {
            gameEngine.start();
        } catch (final Exception exception) {
            LOGGER.error("run() - Failed to run application");
            // System.exit(-1);
            throw new RuntimeException("Failed to run application");
        }
    }
}