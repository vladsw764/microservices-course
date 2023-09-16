package com.isariev.customerservice.dto;

public record OrderDetailsDto(
        String deliveryCountry,
        String deliveryCity,
        String deliveryStreet,
        String deliveryAddress,
        String userId,
        Long orderId
) {
}
