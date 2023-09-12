package com.isariev.orderservice.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;
    private final static String TOPIC = "order-inventory-topic";
    private final static String TOPIC_NEW = "order-inventory-topic-1";


    @Transactional(propagation = Propagation.MANDATORY)
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        Order createOrder = saveOrder(orderRequest, order);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
        OrderResponseDto responseDto = new OrderResponseDto(createOrder.getId(), skuCodes);

        kafkaTemplate.send(TOPIC, responseDto);
    }

    private Order saveOrder(OrderRequest orderRequest, Order order) {
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream().map(OrderMapper::mapToEntity)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        order.setStatus("SUCCESS");

        return orderRepository.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = TOPIC_NEW, groupId = "groupId")
    public void consumeSkuCodes(Long orderId) {
        orderRepository.updateStatusById("FAILED", orderId);
    }
}
