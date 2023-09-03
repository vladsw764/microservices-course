package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.exception.ProductNotExistException;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

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
}
