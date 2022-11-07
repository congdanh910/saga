package com.sg.ws.saga.common.command.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RejectOrderCommand {

    private String orderId;
    private String reason;
    private boolean isRevertProductReservation = Boolean.TRUE; // Always reverts product reservation as default

    public RejectOrderCommand(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
}
