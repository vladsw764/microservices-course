package com.isariev.orderservice.service;

import com.isariev.orderservice.dto.OrderDetailsDto;
import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.dto.OrderResponseDto;
import com.isariev.orderservice.dto.mapper.OrderMapper;
import com.isariev.orderservice.model.Order;
import com.isariev.orderservice.model.OrderLineItems;
import com.isariev.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;
    private final KafkaTemplate<String, OrderDetailsDto> orderInfoDtoKafkaTemplate;
    private final static String INVENTORY_TOPIC = "inventory-order-topic";
    private final static String ORDER_TOPIC = "order-inventory-topic";
    private final static String CUSTOMER_TOPIC = "customer-order-topic";

    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder(OrderRequest orderRequest, String userId) {
        Order order = createAndSaveOrder(orderRequest);
        List<String> skuCodes = extractSkuCodes(order);

        sendOrderResponse(order, skuCodes);
        sendOrderDetails(orderRequest, userId, order.getId());
    }

    private void sendOrderResponse(Order order, List<String> skuCodes) {
        OrderResponseDto responseDto = new OrderResponseDto(order.getId(), skuCodes);
        kafkaTemplate.send(INVENTORY_TOPIC, responseDto);
    }

    private void sendOrderDetails(OrderRequest orderRequest, String userId, Long orderId) {
        OrderDetailsDto orderDetailsDto = createOrderDetailsDto(orderRequest, userId, orderId);
        orderInfoDtoKafkaTemplate.send(CUSTOMER_TOPIC, orderDetailsDto);
    }

    private Order createAndSaveOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(OrderMapper::mapToEntity)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);
        order.setStatus("SUCCESS");

        return orderRepository.save(order);
    }

    private OrderDetailsDto createOrderDetailsDto(OrderRequest orderRequest, String userId, Long orderId) {
        return new OrderDetailsDto(
                orderRequest.getDeliveryInfo().deliveryCountry(),
                orderRequest.getDeliveryInfo().deliveryCity(),
                orderRequest.getDeliveryInfo().deliveryStreet(),
                orderRequest.getDeliveryInfo().deliveryAddress(),
                userId,
                orderId
        );
    }

    private List<String> extractSkuCodes(Order order) {
        return order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = ORDER_TOPIC, groupId = "groupId")
    public void consumeSkuCodes(Long orderId) {
        orderRepository.updateStatusById("FAILED", orderId);
    }
}
