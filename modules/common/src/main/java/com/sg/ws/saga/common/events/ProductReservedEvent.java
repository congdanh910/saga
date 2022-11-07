package com.sg.ws.saga.common.events;

import com.sg.ws.saga.common.BasedOrder;

import lombok.ToString;

@ToString(callSuper = true)
public class ProductReservedEvent extends BasedOrder {

    public ProductReservedEvent() {
        super();
    }
}
