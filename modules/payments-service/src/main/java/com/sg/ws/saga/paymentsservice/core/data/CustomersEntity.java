package com.sg.ws.saga.paymentsservice.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.sg.ws.saga.common.core.data.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_customers")
@AllArgsConstructor
@NoArgsConstructor
public class CustomersEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6594564448986210898L;

    @Column(unique = true)
    private String name;

    @Column(name = "amount_available")
    private int amountAvailable;

    @Column(name = "amount_reserved")
    private int amountReserved;

}
