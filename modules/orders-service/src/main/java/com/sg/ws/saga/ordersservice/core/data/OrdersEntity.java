package com.sg.ws.saga.ordersservice.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.sg.ws.saga.common.core.data.BaseEntity;
import com.sg.ws.saga.ordersservice.core.model.OrderStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "tbl_orders")
@ToString
public class OrdersEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;
    
    @Column(name = "product_id")
    private String productId;
    private int quantity;
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    
    private String reason;
}
