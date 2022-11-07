package com.sg.ws.saga.stocksservice.command.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sg.ws.saga.stocksservice.core.model.ProductsDto;
import com.sg.ws.saga.stocksservice.core.service.ProductsService;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;
    
    @GetMapping("/{id}")
    public Object get(@PathVariable final String id) {
        Optional<ProductsDto> optProductsDto = productsService.findById(id);
        if (optProductsDto.isPresent()) {
            return optProductsDto.get();
        }
        return String.format("Product with given id [%s] not found", id);
    }
    
    @GetMapping
    public List<ProductsDto> getProducts() {
        return productsService.findAll();
    }
}
