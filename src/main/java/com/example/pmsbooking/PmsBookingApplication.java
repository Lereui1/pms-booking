package com.example.pmsbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PmsBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmsBookingApplication.class, args);
    }
}
