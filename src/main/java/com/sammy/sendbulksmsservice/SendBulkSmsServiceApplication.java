package com.sammy.sendbulksmsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SendBulkSmsServiceApplication {


    public static Executor executor = Executors.newFixedThreadPool(300);

    public static void main(String[] args) {
        SpringApplication.run(SendBulkSmsServiceApplication.class, args);
    }

}
