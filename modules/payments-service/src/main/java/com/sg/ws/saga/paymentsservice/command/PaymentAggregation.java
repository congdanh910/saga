package com.sg.ws.saga.paymentsservice.command;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.sg.ws.saga.common.command.commands.ProcessPaymentCommand;
import com.sg.ws.saga.common.config.TopicConfig;
import com.sg.ws.saga.common.events.PaymentCancelledEvent;
import com.sg.ws.saga.common.events.PaymentProceesedEvent;
import com.sg.ws.saga.paymentsservice.core.model.CustomersDto;
import com.sg.ws.saga.paymentsservice.core.model.PaymentsDto;
import com.sg.ws.saga.paymentsservice.core.service.CustomersService;
import com.sg.ws.saga.paymentsservice.core.service.PaymentsService;

@Component
@KafkaListener(id = "paymentsServiceGroup", topics = "${com.sg.ws.saga.topic.payments-service-topic}")
public class PaymentAggregation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAggregation.class);

    private final CustomersService customersService;
    private final PaymentsService paymentsService;
    private final KafkaTemplate<String, Object> template;

    @Autowired
    private TopicConfig topicConfig;

    public PaymentAggregation(final CustomersService customersService, final PaymentsService paymentsService,
            final KafkaTemplate<String, Object> template) {
        this.customersService = customersService;
        this.paymentsService = paymentsService;
        this.template = template;
    }

    @KafkaHandler
    private void handleProcessPaymentCommand(ProcessPaymentCommand processPaymentCommand) {
        LOGGER.debug("Received processPaymentCommand: {}", processPaymentCommand);

        // In this demo, we will update customer amount
        Optional<CustomersDto> optCustomersDto = customersService.findById(processPaymentCommand.getCustomerId());
        if (!optCustomersDto.isPresent()) {
            String reason = String.format("Customer with given id [{}] not found",
                    processPaymentCommand.getCustomerId());
            LOGGER.error(reason);
            sendPaymentCancelledEvent(processPaymentCommand, reason);
            return;
        }

        CustomersDto customersDto = optCustomersDto.get();
        if (customersDto.getAmountAvailable() < processPaymentCommand.getPrice()) {
            String reason = String.format(
                    "Customer does not have enough money. Available amount: %d, requested amount: %d",
                    customersDto.getAmountAvailable(), processPaymentCommand.getPrice());
            LOGGER.error(reason);
            // Not enough money
            sendPaymentCancelledEvent(processPaymentCommand, reason);
            return;
        }

        // Update customer's amount
        customersService.updateCustomerAmount(processPaymentCommand);

        // Create a payment record
        Optional<PaymentsDto> optPaymentsDto = paymentsService.createPayment(processPaymentCommand);

        if (optPaymentsDto.isPresent()) {
            // Send payment proceeded event
            paymentProceesedEvent(processPaymentCommand, optPaymentsDto.get().getId());
        }
    }

    @KafkaHandler(isDefault = true)
    private void unknown(Object object) {
        LOGGER.error("Unkown type received: {}", object);
    }

    private void paymentProceesedEvent(final ProcessPaymentCommand processPaymentCommand, final String paymentId) {
        // Send approval order command to the orders service
        PaymentProceesedEvent paymentProceesedEvent = new PaymentProceesedEvent(processPaymentCommand.getOrderId(),
                paymentId);
        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceBusTopic(),
                paymentProceesedEvent.getOrderId(), paymentProceesedEvent);
        LOGGER.debug("Send paymentProceesedEvent: {}", paymentProceesedEvent);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                LOGGER.info("Sent message=[{}] with offset=[{}], partition [{}]", paymentProceesedEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.info("Unable to send message=[{}] due to : {}", paymentProceesedEvent, ex.getMessage());
            }
        });
    }

    private void sendPaymentCancelledEvent(final ProcessPaymentCommand processPaymentCommand, final String reason) {
        PaymentCancelledEvent paymentCancelledEvent = new PaymentCancelledEvent();
        paymentCancelledEvent.setOrderId(processPaymentCommand.getOrderId());
        paymentCancelledEvent.setProductId(processPaymentCommand.getProductId());
        paymentCancelledEvent.setQuantity(processPaymentCommand.getQuantity());
        paymentCancelledEvent.setPrice(processPaymentCommand.getPrice());
        paymentCancelledEvent.setReason(reason);

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceBusTopic(),
                paymentCancelledEvent.getOrderId(), paymentCancelledEvent);
        LOGGER.debug("Send paymentCancelledEvent: {}", paymentCancelledEvent);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", paymentCancelledEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", paymentCancelledEvent, ex.getMessage());
            }
        });
    }

}
