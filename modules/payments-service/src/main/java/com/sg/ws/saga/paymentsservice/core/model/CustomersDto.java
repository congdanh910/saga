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
@JsonPropertyOrder({ "id", "name", "amount_available", "amount_reserved" })
public class CustomersDto {

    private String id;
    private String name;

    @JsonProperty("amount_available")
    private int amountAvailable;

    @JsonProperty("amount_reserved")
    private int amountReserved;
}
