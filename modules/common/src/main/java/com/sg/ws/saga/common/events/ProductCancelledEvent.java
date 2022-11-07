package com.sg.ws.saga.common.events;

import com.sg.ws.saga.common.BasedOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ProductCancelledEvent extends BasedOrder {

    private String reason;
    
    public ProductCancelledEvent() {
        super();
    }
}
