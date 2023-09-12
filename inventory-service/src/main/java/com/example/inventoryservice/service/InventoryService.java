package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.OrderResponseDto;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.internals.Topic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final static String TOPIC = "order-inventory-topic";
    private final static String TOPIC_NEW = "order-inventory-topic-1";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = TOPIC_NEW, groupId = "groupId", containerFactory = "factory")
    public void consumeSkuCodes(OrderResponseDto responseDto) {
        List<InventoryResponse> responses = inventoryRepository
                .findBySkuCodeIn(responseDto.skuCodes())
                .stream().map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
        boolean result = responses.stream().allMatch(InventoryResponse::isInStock);
        if (!result || responses.isEmpty()) {
            kafkaTemplate.send(TOPIC_NEW, responseDto.orderId());
        }
    }
}
