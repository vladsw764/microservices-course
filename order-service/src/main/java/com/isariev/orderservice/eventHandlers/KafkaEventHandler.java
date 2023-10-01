package com.isariev.orderservice.eventHandlers;

import com.isariev.orderservice.config.kafkaConfig.KafkaTopicConfig;
import com.isariev.orderservice.dto.OrderDetailsDto;
import com.isariev.orderservice.dto.OrderResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicConfig topicConfig;
    private static String CUSTOMER_ORDER_TOPIC;

    @PostConstruct
    private void init() {
        CUSTOMER_ORDER_TOPIC = topicConfig.customerOrderTopic().name();
    }

    public void sendOrderResponse(OrderResponseDto orderResponseDto) {
        kafkaTemplate.send(CUSTOMER_ORDER_TOPIC, 1, String.valueOf(orderResponseDto.orderId()), orderResponseDto);
    }

    public void sendOrderDetails(OrderDetailsDto orderDetailsDto) {
        kafkaTemplate.send(CUSTOMER_ORDER_TOPIC, 2, String.valueOf(orderDetailsDto.orderId()), orderDetailsDto);
    }
}