package com.sg.ws.saga.paymentsservice.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "payment_id", "order_id", "customer_id", "amount" })
public class PaymentsDto {

    @JsonProperty("payment_id")
    private String id;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private String customerId;

    private int amount;
}
