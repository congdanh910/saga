package com.sg.ws.saga.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BasedOrder {

    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private int price;
    
    public BasedOrder() {
        super();
    }
}
