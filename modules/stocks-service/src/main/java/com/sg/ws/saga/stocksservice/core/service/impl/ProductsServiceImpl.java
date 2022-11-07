package com.sg.ws.saga.stocksservice.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sg.ws.saga.stocksservice.core.data.ProductsEntity;
import com.sg.ws.saga.stocksservice.core.data.ProductsRepository;
import com.sg.ws.saga.stocksservice.core.model.ProductsDto;
import com.sg.ws.saga.stocksservice.core.service.ProductsService;

@Service
public class ProductsServiceImpl implements ProductsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private final ProductsRepository productsRepository;

    public ProductsServiceImpl(final ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public Optional<ProductsDto> findById(String id) {
        Optional<ProductsEntity> optProductsEntity = productsRepository.findById(id);
        if (optProductsEntity.isPresent()) {
            ProductsDto dto = new ProductsDto();
            BeanUtils.copyProperties(optProductsEntity.get(), dto);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<ProductsDto> findAll() {
        List<ProductsEntity> products = productsRepository.findAll();
        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProductsDto> items = new ArrayList<>();
        for (ProductsEntity entity : products) {
            ProductsDto dto = new ProductsDto();
            BeanUtils.copyProperties(entity, dto);
            items.add(dto);
        }
        return items;
    }

    @Override
    public void adjustProductAmount(final String productId, final int amount) {
        Optional<ProductsEntity> optProductsEntity = productsRepository.findById(productId);
        if (optProductsEntity.isPresent()) {
            ProductsEntity productsEntity = optProductsEntity.get();
            productsEntity.setQuantity(productsEntity.getQuantity() + amount);
            productsRepository.save(productsEntity);
            LOGGER.debug("Adjusted amount [{}] for product: {}", amount, productsEntity);
        } else {
            LOGGER.info("Product with given id [{}] not found", productId);
        }
    }
}
