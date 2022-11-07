package com.sg.ws.saga.common.command.commands;

import com.sg.ws.saga.common.BasedOrder;

import lombok.ToString;

@ToString(callSuper = true)
public class ProcessPaymentCommand extends BasedOrder {

    public ProcessPaymentCommand() {
        super();
    }
}
