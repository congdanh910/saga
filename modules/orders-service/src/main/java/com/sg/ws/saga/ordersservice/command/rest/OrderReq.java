package com.sg.ws.saga.ordersservice.command.rest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class OrderReq {

    @NotBlank
    @JsonProperty("customer_id")
    private String customerId;

    @NotBlank
    @JsonProperty("product_id")
    private String productId;

    @Min(1)
    private int quantity;

    @Min(1)
    private int price;
}
