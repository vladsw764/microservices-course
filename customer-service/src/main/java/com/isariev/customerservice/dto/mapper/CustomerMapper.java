package com.isariev.customerservice.dto.mapper;

import com.isariev.customerservice.dto.OrderDetailsDto;
import com.isariev.customerservice.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer mapToEntity(OrderDetailsDto dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setUserId(dto.userId());
        customer.setDeliveryCountry(dto.deliveryCountry());
        customer.setDeliveryCity(dto.deliveryCity());
        customer.setDeliveryStreet(dto.deliveryStreet());
        customer.setDeliveryAddress(dto.deliveryAddress());
        customer.setOrderId(dto.orderId());
        customer.setOrderDate();

        return customer;
    }

    public OrderDetailsDto mapToDto(Customer entity) {
        if (entity == null) {
            return null;
        }

        return new OrderDetailsDto(
                entity.getDeliveryCountry(),
                entity.getDeliveryCity(),
                entity.getDeliveryStreet(),
                entity.getDeliveryAddress(),
                entity.getUserId(),
                entity.getOrderId()
        );
    }
}

