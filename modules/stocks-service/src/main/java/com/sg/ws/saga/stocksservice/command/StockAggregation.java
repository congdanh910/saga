package com.sg.ws.saga.stocksservice.command;

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

import com.sg.ws.saga.common.command.commands.CancelProductReservationCommand;
import com.sg.ws.saga.common.command.commands.ReserveProductCommand;
import com.sg.ws.saga.common.config.TopicConfig;
import com.sg.ws.saga.common.events.ProductCancelledEvent;
import com.sg.ws.saga.common.events.ProductReservedEvent;
import com.sg.ws.saga.stocksservice.core.model.ProductsDto;
import com.sg.ws.saga.stocksservice.core.service.ProductsService;

@Component
@KafkaListener(id = "stocksServiceGroup", topics = "${com.sg.ws.saga.topic.stocks-service-topic}")
public class StockAggregation {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockAggregation.class);

    private final ProductsService productsService;
    private final KafkaTemplate<String, Object> template;

    @Autowired
    private TopicConfig topicConfig;

    public StockAggregation(final ProductsService productsService, final KafkaTemplate<String, Object> template) {
        this.productsService = productsService;
        this.template = template;
    }

    @KafkaHandler
    public void handleReserveProductCommand(final ReserveProductCommand reserveProductCommand) {
        LOGGER.debug("Received reserveProductCommand: {}", reserveProductCommand);

        Optional<ProductsDto> optProductsDto = productsService.findById(reserveProductCommand.getProductId());
        if (!optProductsDto.isPresent()) {
            LOGGER.error("Product with given id [%s] is not available: {}", reserveProductCommand.getOrderId());
            // Reject order because product does not exist
            sendProductCancelledEvent(reserveProductCommand,
                    String.format("Product with given id [%s] is not available", reserveProductCommand.getOrderId()));
            return;
        }

        ProductsDto productsDto = optProductsDto.get();
        if (productsDto.getQuantity() < reserveProductCommand.getQuantity()) {
            LOGGER.error("Stock does not have enough product=[{}], reserveProductCommand[{}]", productsDto,
                    reserveProductCommand);
            // Reject order because stock does not have enough product
            String reason = String.format(
                    "Stock does not have enough product. Available amount: %d, requested ammount: %d",
                    productsDto.getQuantity(), reserveProductCommand.getQuantity());
            sendProductCancelledEvent(reserveProductCommand, reason);
            return;
        }

        // Update product quantity
        productsService.adjustProductAmount(reserveProductCommand.getProductId(), (-1 * reserveProductCommand.getQuantity()));

        // Send ProductReservedEvent to the orders service
        sendProductReservedEvent(reserveProductCommand);
    }

    @KafkaHandler
    public void handleCancelProductReservationCommand(
            final CancelProductReservationCommand cancelProductReservationCommand) {
        LOGGER.debug("Received cancelProductReservationCommand: {}", cancelProductReservationCommand);
        productsService.adjustProductAmount(cancelProductReservationCommand.getProductId(),
                cancelProductReservationCommand.getQuantity());
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        LOGGER.error("Unkown type received: {}", object);
    }

    private void sendProductReservedEvent(final ReserveProductCommand reserveProductCommand) {
        ProductReservedEvent productReservedEvent = new ProductReservedEvent();
        productReservedEvent.setOrderId(reserveProductCommand.getOrderId());
        productReservedEvent.setCustomerId(reserveProductCommand.getCustomerId());
        productReservedEvent.setProductId(reserveProductCommand.getProductId());
        productReservedEvent.setQuantity(reserveProductCommand.getQuantity());
        productReservedEvent.setPrice(reserveProductCommand.getPrice());

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceBusTopic(),
                productReservedEvent.getOrderId(), productReservedEvent);
        LOGGER.debug("Send productReservedEvent: {}", productReservedEvent);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", productReservedEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", productReservedEvent, ex.getMessage());
            }
        });
    }

    private void sendProductCancelledEvent(final ReserveProductCommand reserveProductCommand, final String reason) {
        ProductCancelledEvent productCancelledEvent = new ProductCancelledEvent();
        productCancelledEvent.setOrderId(reserveProductCommand.getOrderId());
        productCancelledEvent.setReason(reason);

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceBusTopic(),
                productCancelledEvent.getOrderId(), productCancelledEvent);
        LOGGER.debug("Send productCancelledEvent: {}", productCancelledEvent);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOGGER.debug("Sent message=[{}] with offset=[{}], partition [{}]", productCancelledEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send message=[{}] due to : {}", productCancelledEvent, ex.getMessage());
            }
        });
    }
}
