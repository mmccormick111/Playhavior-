package com.playhavior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point: main() starts Spring and the Tomcat web server on port 8080.
 * WHY this class sits in the top package: @SpringBootApplication scans
 *     com.playhavior and everything below it for @Controller, @Service, @Entity...
 */
@SpringBootApplication
public class PlayhaviorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayhaviorApplication.class, args);
    }
}
