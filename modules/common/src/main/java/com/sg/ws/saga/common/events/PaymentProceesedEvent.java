package com.sg.ws.saga.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProceesedEvent {

    private String orderId;
    private String paymentId;
}
