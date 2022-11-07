package com.sg.ws.saga.ordersservice.core.model;

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
@JsonPropertyOrder({ "order_id", "product_id", "quantity", "price", "order_status" })
public class OrdersDto {

    @JsonProperty("order_id")
    private String id;
    @JsonProperty("product_id")
    private String productId;
    private int quantity;
    private int price;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    private String reason;
}
