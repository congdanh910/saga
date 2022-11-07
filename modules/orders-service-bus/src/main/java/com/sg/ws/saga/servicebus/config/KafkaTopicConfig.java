package com.sg.ws.saga.servicebus.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import com.sg.ws.saga.common.config.TopicConfig;

@Configuration
public class KafkaTopicConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private final TopicConfig topicConfig;
    
    public KafkaTopicConfig(final TopicConfig topicConfig) {
        this.topicConfig = topicConfig;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        LOGGER.debug(">>> kafkaAdmin - configs: {}", configs);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic ordersServiceTopic() {
        return TopicBuilder.name(topicConfig.getOrdersServiceTopic()).compact().build();
    }

    @Bean
    public NewTopic ordersServiceBusTopic() {
        return TopicBuilder.name(topicConfig.getOrdersServiceBusTopic()).compact().build();
    }

    @Bean
    public NewTopic stocksServiceTopic() {
        return TopicBuilder.name(topicConfig.getStocksServiceTopic()).compact().build();
    }

    @Bean
    public NewTopic paymentsServiceTopic() {
        return TopicBuilder.name(topicConfig.getPaymentsServiceTopic()).compact().build();
    }

}
