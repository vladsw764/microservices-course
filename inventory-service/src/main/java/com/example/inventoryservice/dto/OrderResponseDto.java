package com.example.inventoryservice.dto;

import java.util.List;

public record OrderResponseDto(

        Long orderId,

        List<String> skuCodes
) {
}
