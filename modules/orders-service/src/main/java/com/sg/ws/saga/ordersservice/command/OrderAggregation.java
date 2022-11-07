package com.sg.ws.saga.ordersservice.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sg.ws.saga.common.command.commands.ApproveOrderCommand;
import com.sg.ws.saga.common.command.commands.RejectOrderCommand;
import com.sg.ws.saga.ordersservice.core.model.OrderStatus;
import com.sg.ws.saga.ordersservice.core.service.OrdersService;

@Component
@KafkaListener(id = "ordersServiceGroup", topics = "${com.sg.ws.saga.topic.orders-service-topic}")
public class OrderAggregation {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAggregation.class);

    private final OrdersService ordersService;

    public OrderAggregation(final OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @KafkaHandler
    public void handleApproveOrderCommand(final ApproveOrderCommand approveOrderCommand) {
        LOGGER.debug("Received approveOrderCommand: {}", approveOrderCommand);
        ordersService.updateOrder(approveOrderCommand.getOrderId(), OrderStatus.APPROVED, "");
    }

    @KafkaHandler
    public void handleRejectOrderCommand(final RejectOrderCommand rejectOrderCommand) {
        LOGGER.debug("Received rejectOrderCommand: {}", rejectOrderCommand);
        ordersService.updateOrder(rejectOrderCommand.getOrderId(), OrderStatus.REJECTED,
                rejectOrderCommand.getReason());
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Object object) {
        LOGGER.error("Unkown type received: {}", object);
    }

}
