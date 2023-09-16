package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.OrderResponseDto;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final static String ORDER_TOPIC = "order-inventory-topic";
    private final static String INVENTORY_TOPIC = "inventory-order-topic";


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = INVENTORY_TOPIC, groupId = "groupId", containerFactory = "factory")
    public void consumeSkuCodes(OrderResponseDto responseDto) {
        List<InventoryResponse> responses = fetchInventoryResponse(responseDto);
        throw new RuntimeException("exception");
//        if (!responses.stream().allMatch(InventoryResponse::isInStock)) {
//            kafkaTemplate.send(ORDER_TOPIC, responseDto.orderId());
//        }
    }

    private List<InventoryResponse> fetchInventoryResponse(OrderResponseDto responseDto) {
        return inventoryRepository
                .findBySkuCodeIn(responseDto.skuCodes())
                .stream()
                .map(this::mapToInventoryResponse)
                .toList();
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
