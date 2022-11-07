package com.sg.ws.saga.common.command.commands;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ReserveProductCommand {

    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private int price;
}
