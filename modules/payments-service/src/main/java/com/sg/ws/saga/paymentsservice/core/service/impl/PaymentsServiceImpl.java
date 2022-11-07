package com.sg.ws.saga.paymentsservice.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.sg.ws.saga.common.command.commands.ProcessPaymentCommand;
import com.sg.ws.saga.paymentsservice.core.data.PaymentRepository;
import com.sg.ws.saga.paymentsservice.core.data.PaymentsEntity;
import com.sg.ws.saga.paymentsservice.core.model.PaymentsDto;
import com.sg.ws.saga.paymentsservice.core.service.PaymentsService;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentRepository paymentRepository;

    public PaymentsServiceImpl(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<PaymentsDto> createPayment(final ProcessPaymentCommand processPaymentCommand) {
        PaymentsEntity entity = new PaymentsEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setOrderId(processPaymentCommand.getOrderId());
        entity.setCustomerId(processPaymentCommand.getCustomerId());
        entity.setAmount(processPaymentCommand.getPrice());
        entity = paymentRepository.save(entity);
        PaymentsDto dto = new PaymentsDto();
        BeanUtils.copyProperties(entity, dto);
        return Optional.of(dto);
    }

    @Override
    public Optional<PaymentsDto> findById(String id) {
        Optional<PaymentsEntity> optPaymentsEntity = paymentRepository.findById(id);
        if (optPaymentsEntity.isPresent()) {
            PaymentsDto dto = new PaymentsDto();
            BeanUtils.copyProperties(optPaymentsEntity.get(), dto);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<PaymentsDto> findAll() {
        List<PaymentsEntity> payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            return Collections.emptyList();
        }

        List<PaymentsDto> items = new ArrayList<>();
        for (PaymentsEntity entity : payments) {
            PaymentsDto dto = new PaymentsDto();
            BeanUtils.copyProperties(entity, dto);
            items.add(dto);
        }
        return items;
    }

}
