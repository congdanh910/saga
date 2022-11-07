package com.sg.ws.saga.ordersservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrdersEntity, String> {
    
}
