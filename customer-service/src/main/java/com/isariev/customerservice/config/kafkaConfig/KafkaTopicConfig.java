package com.isariev.customerservice.config.kafkaConfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderInventoryTopic() {
        return TopicBuilder.name("order-inventory-topic")
                .build();
    }
}
