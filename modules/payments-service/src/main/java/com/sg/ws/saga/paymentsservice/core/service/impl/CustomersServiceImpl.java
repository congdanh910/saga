package com.sg.ws.saga.paymentsservice.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sg.ws.saga.common.command.commands.ProcessPaymentCommand;
import com.sg.ws.saga.paymentsservice.core.data.CustomersEntity;
import com.sg.ws.saga.paymentsservice.core.data.CustomersRepository;
import com.sg.ws.saga.paymentsservice.core.model.CustomersDto;
import com.sg.ws.saga.paymentsservice.core.service.CustomersService;

@Service
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;

    public CustomersServiceImpl(final CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    @Override
    public void updateCustomerAmount(final ProcessPaymentCommand processPaymentCommand) {
        Optional<CustomersEntity> optCustomersEntity = customersRepository
                .findById(processPaymentCommand.getCustomerId());
        if (optCustomersEntity.isPresent()) {
            CustomersEntity customersEntity = optCustomersEntity.get();
            customersEntity.setAmountAvailable(customersEntity.getAmountAvailable() - processPaymentCommand.getPrice());
            customersEntity.setAmountReserved(customersEntity.getAmountReserved() + processPaymentCommand.getPrice());
            customersRepository.save(customersEntity);
        }
    }

    @Override
    public Optional<CustomersDto> findById(String id) {
        Optional<CustomersEntity> optCustomersEntity = customersRepository.findById(id);
        if (optCustomersEntity.isPresent()) {
            CustomersDto dto = new CustomersDto();
            BeanUtils.copyProperties(optCustomersEntity.get(), dto);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<CustomersDto> findAll() {
        List<CustomersEntity> customers = customersRepository.findAll();
        if (customers.isEmpty()) {
            return Collections.emptyList();
        }

        List<CustomersDto> items = new ArrayList<>();
        for (CustomersEntity entity : customers) {
            CustomersDto dto = new CustomersDto();
            BeanUtils.copyProperties(entity, dto);
            items.add(dto);
        }
        return items;
    }

}
