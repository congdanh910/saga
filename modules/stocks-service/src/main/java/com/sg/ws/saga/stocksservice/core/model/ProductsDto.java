package com.sg.ws.saga.stocksservice.core.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "name", "quantity", "price" })
public class ProductsDto {

    private String id;
    private String name;
    private int quantity;
    private int price;
}
