package com.isariev.orderservice.service;

import com.isariev.coresecurity.AuthService;
import com.isariev.orderservice.dto.OrderDetailsDto;
import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.dto.OrderResponseDto;
import com.isariev.orderservice.dto.mapper.OrderMapper;
import com.isariev.orderservice.eventHandlers.KafkaEventHandler;
import com.isariev.orderservice.model.Order;
import com.isariev.orderservice.model.OrderLineItems;
import com.isariev.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final static String INVENTORY_TOPIC = "inventory-topic";


    private final OrderRepository orderRepository;
    private final KafkaEventHandler eventHandler;
    private final AuthService authService;


    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder(final OrderRequest orderRequest) {
        Order order = createAndSaveOrder(orderRequest);
        List<String> skuCodes = extractSkuCodes(order);

        sendOrderResponse(order, skuCodes);
        sendOrderDetails(orderRequest, order.getId());
    }

    private void sendOrderResponse(final Order order, final List<String> skuCodes) {
        OrderResponseDto responseDto = new OrderResponseDto(order.getId(), skuCodes);
        LOGGER.info("Send order response with id: {} -> to inventory service", responseDto.orderId());
        this.eventHandler.sendOrderResponse(responseDto);
    }

    private void sendOrderDetails(final OrderRequest orderRequest, final Long orderId) {
        OrderDetailsDto orderDetailsDto = createOrderDetailsDto(orderRequest, orderId);
        LOGGER.info("Send order details with id: {} and userId: {} -> to inventory service", orderDetailsDto.orderId(), orderDetailsDto.userId());
        this.eventHandler.sendOrderDetails(orderDetailsDto);
    }

    private Order createAndSaveOrder(final OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(OrderMapper::mapToEntity)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);
        order.setStatus("SUCCESS");
        LOGGER.info("Saving order with order number: {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    private OrderDetailsDto createOrderDetailsDto(final OrderRequest orderRequest, final Long orderId) {
        LOGGER.info("sub: " + authService.getUserSub());

        return new OrderDetailsDto(
                orderRequest.getDeliveryInfo().deliveryCountry(),
                orderRequest.getDeliveryInfo().deliveryCity(),
                orderRequest.getDeliveryInfo().deliveryStreet(),
                orderRequest.getDeliveryInfo().deliveryAddress(),
                authService.getUserSub(),
                null,
                null,
                orderId
        );
    }

    private List<String> extractSkuCodes(final Order order) {
        LOGGER.info("Extract sku codes from order: {}", order.getId());
        return order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = INVENTORY_TOPIC, groupId = "groupId")
    public void consumeSkuCodes(@Payload final Long orderId) {
        LOGGER.error("Order was failed with id: {}", orderId);
        orderRepository.updateStatusById("FAILED", orderId);
    }

}
