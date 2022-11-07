package com.sg.ws.saga.paymentsservice.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.sg.ws.saga.common.core.data.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_payments")
public class PaymentsEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5313493413859894403L;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "customer_id")
    private String customerId;

    private int amount;
}
