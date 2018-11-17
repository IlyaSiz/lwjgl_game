package com.sizphoto.shiningproject;

import com.sizphoto.shiningproject.service.ApplicationRunService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootStandaloneApplication implements CommandLineRunner {

    private ApplicationRunService applicationRunService;

    public SpringBootStandaloneApplication(final ApplicationRunService applicationRunService){
        this.applicationRunService = applicationRunService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStandaloneApplication.class, args);
    }

    @Override
    public void run(String... args) {
        applicationRunService.run();
    }
}