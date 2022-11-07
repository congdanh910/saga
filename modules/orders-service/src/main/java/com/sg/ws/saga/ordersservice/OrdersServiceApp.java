package com.sg.ws.saga.ordersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.sg.ws.saga" })
public class OrdersServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(OrdersServiceApp.class, args);
    }

}
