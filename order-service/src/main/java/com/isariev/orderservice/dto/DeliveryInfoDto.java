package com.isariev.orderservice.dto;

public record DeliveryInfoDto(
        String deliveryCountry,
        String deliveryCity,
        String deliveryStreet,
        String deliveryAddress
) {
}
