package com.sg.ws.saga.paymentsservice.core.service;

import java.util.List;
import java.util.Optional;

import com.sg.ws.saga.common.command.commands.ProcessPaymentCommand;
import com.sg.ws.saga.paymentsservice.core.model.PaymentsDto;

public interface PaymentsService {

    Optional<PaymentsDto> createPayment(final ProcessPaymentCommand processPaymentCommand);
    
    Optional<PaymentsDto> findById(String id);

    List<PaymentsDto> findAll();
}
