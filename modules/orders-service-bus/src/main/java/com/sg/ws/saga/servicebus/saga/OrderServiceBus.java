package com.sg.ws.saga.servicebus.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.sg.ws.saga.common.command.commands.ApproveOrderCommand;
import com.sg.ws.saga.common.command.commands.CancelProductReservationCommand;
import com.sg.ws.saga.common.command.commands.ProcessPaymentCommand;
import com.sg.ws.saga.common.command.commands.RejectOrderCommand;
import com.sg.ws.saga.common.command.commands.ReserveProductCommand;
import com.sg.ws.saga.common.config.TopicConfig;
import com.sg.ws.saga.common.events.OrderCreatedEvent;
import com.sg.ws.saga.common.events.PaymentCancelledEvent;
import com.sg.ws.saga.common.events.PaymentProceesedEvent;
import com.sg.ws.saga.common.events.ProductCancelledEvent;
import com.sg.ws.saga.common.events.ProductReservedEvent;

@Component
@KafkaListener(id = "ordersServiceGroup", topics = { "${com.sg.ws.saga.topic.orders-service-bus-topic}" })
public class OrderServiceBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceBus.class);

    private final KafkaTemplate<String, Object> template;

    private final TopicConfig topicConfig;

    public OrderServiceBus(final KafkaTemplate<String, Object> template, final TopicConfig topicConfig) {
        this.template = template;
        this.topicConfig = topicConfig;
    }

    @KafkaHandler
    public void handleOrderCreatedEvent(final OrderCreatedEvent orderCreatedEvent) {
        LOGGER.debug("Received orderCreatedEvent: {}", orderCreatedEvent);

        ReserveProductCommand reserveProductCommand = new ReserveProductCommand();
        reserveProductCommand.setOrderId(orderCreatedEvent.getOrderId());
        reserveProductCommand.setCustomerId(orderCreatedEvent.getCustomerId());
        reserveProductCommand.setProductId(orderCreatedEvent.getProductId());
        reserveProductCommand.setQuantity(orderCreatedEvent.getQuantity());
        reserveProductCommand.setPrice(orderCreatedEvent.getPrice());

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getStocksServiceTopic(),
                reserveProductCommand.getOrderId(), reserveProductCommand);
        LOGGER.debug("Send reserveProductCommand: {}", reserveProductCommand);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", reserveProductCommand,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", reserveProductCommand, ex.getMessage());
                // Order just created with status Created. No need to update
            }
        });
    }

    @KafkaHandler
    public void handleProductReservedEvent(final ProductReservedEvent productReservedEvent) {
        LOGGER.debug("Received productReservedEvent: {}", productReservedEvent);

        // Handle payment
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand();
        processPaymentCommand.setOrderId(productReservedEvent.getOrderId());
        processPaymentCommand.setCustomerId(productReservedEvent.getCustomerId());
        processPaymentCommand.setProductId(productReservedEvent.getProductId());
        processPaymentCommand.setQuantity(productReservedEvent.getQuantity());
        processPaymentCommand.setPrice(productReservedEvent.getPrice());

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getPaymentsServiceTopic(),
                processPaymentCommand.getOrderId(), processPaymentCommand);
        LOGGER.debug("Send processPaymentCommand: {}", processPaymentCommand);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", processPaymentCommand,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", processPaymentCommand, ex.getMessage());

                // Revert product quantity
                CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand();
                cancelProductReservationCommand.setOrderId(productReservedEvent.getOrderId());
                cancelProductReservationCommand.setProductId(productReservedEvent.getProductId());
                cancelProductReservationCommand.setQuantity(productReservedEvent.getQuantity());
                cancelProductReservationCommand.setReason("Cannot send payment-command to the payments-service topic");
                cancelProductReservationCommand(cancelProductReservationCommand);
            }
        });
    }

    @KafkaHandler
    public void handleProductCancelledEvent(final ProductCancelledEvent productCancelledEvent) {
        LOGGER.debug("Received productCancelledEvent: {}", productCancelledEvent);
        // Cannot reserve the product. So order should be rejected
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand();
        rejectOrderCommand.setOrderId(productCancelledEvent.getOrderId());
        rejectOrderCommand.setReason(productCancelledEvent.getReason());
        rejectOrderCommand.setRevertProductReservation(Boolean.FALSE);
        sendRejectOrderCommand(rejectOrderCommand);
    }

    @KafkaHandler
    public void handlePaymentProceesedEvent(final PaymentProceesedEvent paymentProceesedEvent) {
        LOGGER.debug("Received paymentProceesedEvent: {}", paymentProceesedEvent);
        
        // Send approval order command to the orders service
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProceesedEvent.getOrderId());
        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceTopic(),
                approveOrderCommand.getOrderId(), approveOrderCommand);
        LOGGER.debug("Send approveOrderCommand: {}", approveOrderCommand);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", paymentProceesedEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", paymentProceesedEvent, ex.getMessage());
            }
        });
    }

    @KafkaHandler
    public void handlePaymentCancelledEvent(final PaymentCancelledEvent paymentCancelledEvent) {
        LOGGER.debug("Received paymentCancelledEvent: {}", paymentCancelledEvent);
        
        // Reject order
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand();
        rejectOrderCommand.setOrderId(paymentCancelledEvent.getOrderId());
        rejectOrderCommand.setReason(paymentCancelledEvent.getReason());
        rejectOrderCommand.setRevertProductReservation(Boolean.FALSE);
        sendRejectOrderCommand(rejectOrderCommand);

        // Cancel product reservation
        CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand();
        cancelProductReservationCommand.setOrderId(paymentCancelledEvent.getOrderId());
        cancelProductReservationCommand.setProductId(paymentCancelledEvent.getProductId());
        cancelProductReservationCommand.setQuantity(paymentCancelledEvent.getQuantity());
        cancelProductReservationCommand.setReason(paymentCancelledEvent.getReason());
        cancelProductReservationCommand(cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleRejectOrderCommand(final RejectOrderCommand rejectOrderCommand) {
        LOGGER.debug("Received rejectOrderCommand: {}", rejectOrderCommand);

    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        LOGGER.error("Unkown type received: {}", object);
    }

    private void sendRejectOrderCommand(final RejectOrderCommand rejectOrderCommand) {
        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceTopic(),
                rejectOrderCommand.getOrderId(), rejectOrderCommand);
        LOGGER.debug("Send rejectOrderCommand: {}", rejectOrderCommand);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", rejectOrderCommand,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", rejectOrderCommand, ex.getMessage());
            }
        });
    }

    private void cancelProductReservationCommand(
            final CancelProductReservationCommand cancelProductReservationCommand) {
        
        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getStocksServiceTopic(),
                cancelProductReservationCommand.getOrderId(), cancelProductReservationCommand);
        LOGGER.debug("Send cancelProductReservationCommand: {}", cancelProductReservationCommand);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", cancelProductReservationCommand,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", cancelProductReservationCommand,
                        ex.getMessage());
            }
        });
    }

}
