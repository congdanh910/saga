package com.sg.ws.saga.servicebus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.sg.ws.saga" })
public class ServiceBusApp {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBusApp.class, args);
    }

}
