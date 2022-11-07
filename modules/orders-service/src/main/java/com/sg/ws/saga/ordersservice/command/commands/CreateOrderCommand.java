package com.sg.ws.saga.ordersservice.command.commands;

import com.sg.ws.saga.ordersservice.core.model.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CreateOrderCommand {

    private String id; // Order Id
    private String customerId;
    private String productId;
    private int quantity;
    private int price;
    private OrderStatus orderStatus;
}
