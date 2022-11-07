package com.sg.ws.saga.stocksservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<ProductsEntity, String> {

}
