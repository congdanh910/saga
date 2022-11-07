package com.sg.ws.saga.servicebus.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerConfig.class);

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, Object> multiTypeProducerFactory() {

        StringBuilder mappings = new StringBuilder();
        // Use fully-qualified name of a class
        // if message is sent to another topic /or another stream
        mappings.append("com.sg.ws.saga.common.command.commands.ReserveProductCommand:");
        mappings.append("com.sg.ws.saga.common.command.commands.ReserveProductCommand, ");
        mappings.append("com.sg.ws.saga.common.command.commands.ProcessPaymentCommand:");
        mappings.append("com.sg.ws.saga.common.command.commands.ProcessPaymentCommand, ");
        mappings.append("com.sg.ws.saga.common.command.commands.CancelProductReservationCommand:");
        mappings.append("com.sg.ws.saga.common.command.commands.CancelProductReservationCommand, ");
        
        mappings.append("com.sg.ws.saga.common.events.OrderCreatedEvent:");
        mappings.append("com.sg.ws.saga.common.events.OrderCreatedEvent, ");
        mappings.append("com.sg.ws.saga.common.events.PaymentCancelledEvent:");
        mappings.append("com.sg.ws.saga.common.events.PaymentCancelledEvent, ");
        mappings.append("com.sg.ws.saga.common.events.PaymentProceesedEvent:");
        mappings.append("com.sg.ws.saga.common.events.PaymentProceesedEvent, ");
        mappings.append("com.sg.ws.saga.common.events.ProductCancelledEvent:");
        mappings.append("com.sg.ws.saga.common.events.ProductCancelledEvent, ");
        mappings.append("com.sg.ws.saga.common.events.ProductReservedEvent:");
        mappings.append("com.sg.ws.saga.common.events.ProductReservedEvent");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, mappings.toString());

        LOGGER.info("multiTypeProducerFactory - configProps: {}", configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> multiTypeKafkaTemplate() {
        return new KafkaTemplate<>(multiTypeProducerFactory());
    }

}
