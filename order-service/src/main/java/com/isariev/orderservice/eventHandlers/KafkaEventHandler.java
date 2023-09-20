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

    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;
    private final KafkaTemplate<String, OrderDetailsDto> orderInfoDtoKafkaTemplate;
    private final KafkaTopicConfig topicConfig;
    private static String CUSTOMER_ORDER_TOPIC;
    private static String INVENTORY_ORDER_TOPIC;

    @PostConstruct
    private void init() {
        CUSTOMER_ORDER_TOPIC = topicConfig.customerOrderTopic().name();
        INVENTORY_ORDER_TOPIC = topicConfig.inventoryOrderTopic().name();
    }

    public void sendOrderResponse(OrderResponseDto orderResponseDto) {
        kafkaTemplate.send(INVENTORY_ORDER_TOPIC, orderResponseDto);
    }

    public void sendOrderDetails(OrderDetailsDto orderDetailsDto) {
        orderInfoDtoKafkaTemplate.send(CUSTOMER_ORDER_TOPIC, orderDetailsDto);
    }
}
