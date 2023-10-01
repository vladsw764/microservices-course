package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.OrderResponseDto;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final static String ORDER_TOPIC = "order-topic";
    private final static String INVENTORY_TOPIC = "inventory-topic";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(
            topics = ORDER_TOPIC,
            groupId = "groupId",
            containerFactory = "factory",
            topicPartitions = @TopicPartition(topic = ORDER_TOPIC, partitions = {"1"})
    )
    public void consumeSkuCodes(@Payload OrderResponseDto responseDto) {
        log.info("Received order response: {}", responseDto);

        List<InventoryResponse> responses = fetchInventoryResponse(responseDto);

        if (!responses.stream().allMatch(InventoryResponse::isInStock)) {
            log.warn("Not all items are in stock for order {}", responseDto.orderId());
            kafkaTemplate.send(INVENTORY_TOPIC, 0, String.valueOf(responseDto.orderId()), responseDto.orderId());
            log.info("Sent order ID {} to inventory topic for further processing", responseDto.orderId());
        } else {
            log.info("All items are in stock for order {}", responseDto.orderId());
        }
    }

    private List<InventoryResponse> fetchInventoryResponse(OrderResponseDto responseDto) {
        List<InventoryResponse> inventoryResponses = inventoryRepository
                .findBySkuCodeIn(responseDto.skuCodes())
                .stream()
                .map(this::mapToInventoryResponse)
                .toList();

        log.info("Fetched inventory responses: {}", inventoryResponses);

        return inventoryResponses;
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        InventoryResponse inventoryResponse = InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();

        log.info("Mapped inventory item to response: {}", inventoryResponse);

        return inventoryResponse;
    }
}
