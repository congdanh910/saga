package com.sg.ws.saga.ordersservice.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sg.ws.saga.ordersservice.core.data.OrdersEntity;
import com.sg.ws.saga.ordersservice.core.data.OrdersRepository;
import com.sg.ws.saga.ordersservice.core.model.OrderStatus;
import com.sg.ws.saga.ordersservice.core.model.OrdersDto;
import com.sg.ws.saga.ordersservice.core.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    public OrdersServiceImpl(final OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    
    @Override
    public OrdersDto create(final OrdersDto ordersDto) {
        OrdersEntity ordersEntity = new OrdersEntity();
        BeanUtils.copyProperties(ordersDto, ordersEntity);
        ordersEntity = ordersRepository.save(ordersEntity);
        LOGGER.debug("Created orders: {}", ordersEntity);
        
        // Ensure order created
        BeanUtils.copyProperties(ordersEntity, ordersDto);
        return ordersDto;
    }
    
    @Override
    public void updateOrder(final String orderId, final OrderStatus orderStatus, final String reason) {
        Optional<OrdersEntity> optionalOrdersEntity = ordersRepository.findById(orderId);
        if (optionalOrdersEntity.isPresent()) {
            OrdersEntity ordersEntity = optionalOrdersEntity.get();
            ordersEntity.setOrderStatus(orderStatus);
            ordersEntity.setReason(reason);
            ordersRepository.save(ordersEntity);
            LOGGER.debug("Update orders [{}] with status [{}]", ordersEntity, orderStatus);
        } else {
            LOGGER.info("Orders with given id [{}] not found", orderId);
        }
    }

    @Override
    public Optional<OrdersDto> findById(String orderId) {
        Optional<OrdersEntity> optOrdersEntity = ordersRepository.findById(orderId);
        if (optOrdersEntity.isPresent()) {            
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(optOrdersEntity.get(), dto);
            return Optional.ofNullable(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<OrdersDto> findAll() {
        List<OrdersEntity> orders = ordersRepository.findAll();
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrdersDto> items = new ArrayList<>();
        for (OrdersEntity entity : orders) {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(entity, dto);
            items.add(dto);
        }
        return items;
    }

    

}
