package com.sg.ws.saga.stocksservice.core.service;

import java.util.List;
import java.util.Optional;

import com.sg.ws.saga.stocksservice.core.model.ProductsDto;

public interface ProductsService {

    Optional<ProductsDto> findById(String id);

    List<ProductsDto> findAll();
    
    void adjustProductAmount(final String productId, final int amount);
}
