package com.sg.ws.saga.ordersservice.core.service;

import java.util.List;
import java.util.Optional;

import com.sg.ws.saga.ordersservice.core.model.OrderStatus;
import com.sg.ws.saga.ordersservice.core.model.OrdersDto;

public interface OrdersService {

    OrdersDto create(OrdersDto ordersDto);
    
    void updateOrder(String orderId, OrderStatus orderStatus, String reason);
    
    /**
     * Gets order by id
     * 
     * @param orderId
     *            the order id to be looked
     * @return an optional of the {@link OrdersDto}
     */
    Optional<OrdersDto> findById(String orderId);
    
    List<OrdersDto> findAll();

}
