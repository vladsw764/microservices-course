package com.isariev.orderservice.dto.mapper;

import com.isariev.orderservice.dto.OrderLineItemsDto;
import com.isariev.orderservice.model.OrderLineItems;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public static OrderLineItems mapToEntity(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
