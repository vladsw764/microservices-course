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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;
    private final static String TOPIC = "order-inventory-topic";
    private final static String TOPIC_NEW = "order-inventory-topic-1";


    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        try {
            order.setOrderNumber(UUID.randomUUID().toString());
            List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                    .stream().map(OrderMapper::mapToEntity)
                    .toList();
            order.setOrderLineItemsList(orderLineItems);
            order.setStatus("SUCCESS");

            Order savedOrder = orderRepository.save(order);

            List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
            OrderResponseDto responseDto = new OrderResponseDto(savedOrder.getId(), skuCodes);

            kafkaTemplate.send(TOPIC, responseDto);
        } catch (Exception e) {
            order.setStatus("FAILED");
            orderRepository.save(order);
        }
    }

    @KafkaListener(topics = TOPIC_NEW, groupId = "groupId")
    public void consumeSkuCodes(Long orderId) {
        orderRepository.updateStatusById("FAILED", orderId);
    }
}
