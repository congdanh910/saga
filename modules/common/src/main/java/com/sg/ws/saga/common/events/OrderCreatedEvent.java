package com.sg.ws.saga.common.events;

import com.sg.ws.saga.common.BasedOrder;

import lombok.ToString;

@ToString(callSuper = true)
public class OrderCreatedEvent extends BasedOrder {

    public OrderCreatedEvent() {
        super();
    }
}
