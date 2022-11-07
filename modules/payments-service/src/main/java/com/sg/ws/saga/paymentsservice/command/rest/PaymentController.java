package com.sg.ws.saga.paymentsservice.command.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sg.ws.saga.paymentsservice.core.model.CustomersDto;
import com.sg.ws.saga.paymentsservice.core.model.PaymentsDto;
import com.sg.ws.saga.paymentsservice.core.service.CustomersService;
import com.sg.ws.saga.paymentsservice.core.service.PaymentsService;

@RestController
@RequestMapping
public class PaymentController {

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private CustomersService customersService;

    @GetMapping("/payments/{id}")
    public Object getPayment(@PathVariable final String id) {
        Optional<PaymentsDto> optPaymentsDto = paymentsService.findById(id);
        if (optPaymentsDto.isPresent()) {
            return optPaymentsDto.get();
        }
        return String.format("Payment with given id [%] not found", id);
    }

    @GetMapping("/payments")
    public List<PaymentsDto> getPayments() {
        return paymentsService.findAll();
    }

    @GetMapping("/customers/{id}")
    public Object getCustomer(@PathVariable final String id) {
        Optional<CustomersDto> optCustomDto = customersService.findById(id);
        if (optCustomDto.isPresent()) {
            return optCustomDto.get();
        }
        return String.format("Customer with given id [%] not found", id);
    }

    @GetMapping("/customers")
    public List<CustomersDto> getCustomers() {
        return customersService.findAll();
    }

}
