package com.sg.ws.saga.common.command.commands;

import com.sg.ws.saga.common.BasedOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class CancelProductReservationCommand extends BasedOrder {

    private String reason;
    
    public CancelProductReservationCommand() {
        super();
    }
}
