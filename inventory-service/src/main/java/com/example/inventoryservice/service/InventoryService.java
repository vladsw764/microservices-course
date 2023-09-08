package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.exception.ProductNotExistException;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final MessageConverter messageConverter;
    private final static String TOPIC = "order-inventory-topic";
    private final static String TOPIC_NEW = "order-inventory-topic-1";

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Checking inventory for SKUs: {}", skuCode);

        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCode);

        List<InventoryResponse> responses = inventoryList.stream().map(inventory ->
                InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build()
        ).toList();

        log.info("Inventory check completed. Responses: {}", responses);

        if (responses.isEmpty()) {
            throw new ProductNotExistException("Product is not in stock, please try again latter");
        }
        return responses;
    }

    @KafkaListener(topics = TOPIC, groupId = "groupId")
    public void consumeSkuCodes(String resultMap) {
        Map<String, Object> map = messageConverter.convertJsonToMap(resultMap);
        List<InventoryResponse> responses = inventoryRepository
                .findBySkuCodeIn((List<String>) map.get("skuCodes"))
                .stream().map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
        boolean result = responses.stream().allMatch(InventoryResponse::isInStock);
        if (!result || responses.isEmpty()) {
            Integer res = (Integer) map.get("orderId");
            kafkaTemplate.send(TOPIC_NEW, res.longValue());
        }
    }
}
