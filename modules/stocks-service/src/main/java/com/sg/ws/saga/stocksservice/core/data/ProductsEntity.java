package com.sg.ws.saga.stocksservice.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.sg.ws.saga.common.core.data.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tbl_products")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductsEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8378648336511109519L;
    
    @Column(unique = true)
    private String name;
    
    private int quantity;
    private int price;
}
