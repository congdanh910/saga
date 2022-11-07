package com.sg.ws.saga.ordersservice.command.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sg.ws.saga.common.config.TopicConfig;
import com.sg.ws.saga.common.events.OrderCreatedEvent;
import com.sg.ws.saga.ordersservice.core.model.OrderStatus;
import com.sg.ws.saga.ordersservice.core.model.OrdersDto;
import com.sg.ws.saga.ordersservice.core.service.OrdersService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrdersService ordersService;
    private final KafkaTemplate<String, Object> template;
    // private StreamsBuilderFactoryBean kafkaStreamsFactory;

    @Autowired
    private TopicConfig topicConfig;

    public OrderController(final OrdersService ordersService, final KafkaTemplate<String, Object> template) {
        this.ordersService = ordersService;
        this.template = template;
        // this.kafkaStreamsFactory = kafkaStreamsFactory;
        // this.orderGeneratorService = orderGeneratorService;
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable final String id) {
        Optional<OrdersDto> optOrdersDto = ordersService.findById(id);
        if (optOrdersDto.isPresent()) {
            return optOrdersDto.get();
        }
        return String.format("Order with given id [%s] not found", id);
    }

    @GetMapping
    public List<OrdersDto> getOrders() {
        return ordersService.findAll();
    }

    @PostMapping
    public String create(@Valid @RequestBody final OrderReq orderReq) {
        OrdersDto ordersDto = createOrder(orderReq);
        sendOrderCreatedEvent(ordersDto, orderReq.getCustomerId());
        return ordersDto.getId();
    }

    private OrdersDto createOrder(final OrderReq orderReq) {
        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setId(UUID.randomUUID().toString());
        ordersDto.setProductId(orderReq.getProductId());
        ordersDto.setQuantity(orderReq.getQuantity());
        ordersDto.setPrice(orderReq.getPrice());
        ordersDto.setOrderStatus(OrderStatus.CREATED);
        return ordersService.create(ordersDto);
    }

    private void sendOrderCreatedEvent(final OrdersDto ordersDto, final String customerId) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId(ordersDto.getId());
        orderCreatedEvent.setCustomerId(customerId);
        orderCreatedEvent.setProductId(ordersDto.getProductId());
        orderCreatedEvent.setQuantity(ordersDto.getQuantity());
        orderCreatedEvent.setPrice(ordersDto.getPrice());

        LOG.debug("sendOrderCreatedEvent - orderCreatedEvent: {}", orderCreatedEvent);

        ListenableFuture<SendResult<String, Object>> future = template.send(topicConfig.getOrdersServiceBusTopic(),
                orderCreatedEvent.getOrderId(), orderCreatedEvent);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {

                LOG.debug("Sent message=[{}] with offset=[{}], partition [{}]", orderCreatedEvent,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Unable to send message=[{}] due to : {}", orderCreatedEvent, ex.getMessage());
                // Order just created with status Created. No need to update
            }
        });
    }
}
