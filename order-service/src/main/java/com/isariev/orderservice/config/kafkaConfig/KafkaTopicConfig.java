package com.isariev.orderservice.config.kafkaConfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.inventory}")
    private String inventoryTopic;

    @Value("${spring.kafka.topics.order}")
    private String orderTopic;

    @Value("${spring.kafka.topics.customer}")
    private String customerTopic;

    @Bean
    public NewTopic orderInventoryTopic() {
        return TopicBuilder.name(orderTopic)
                .build();
    }

    @Bean
    public NewTopic inventoryOrderTopic() {
        return TopicBuilder.name(inventoryTopic)
                .build();
    }

    @Bean
    public NewTopic customerOrderTopic() {
        return TopicBuilder.name(customerTopic)
                .build();
    }
}
