package com.sg.ws.saga.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.sg.ws.saga.topic")
public class TopicConfig {

    private String ordersServiceTopic;
    private String ordersServiceBusTopic;
    private String stocksServiceTopic;
    private String paymentsServiceTopic;
}
