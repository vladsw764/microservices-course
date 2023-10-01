package com.isariev.orderservice.service;

import com.isariev.orderservice.dto.OrderDetailsDto;
import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.dto.OrderResponseDto;
import com.isariev.orderservice.dto.UserInfoDto;
import com.isariev.orderservice.dto.mapper.OrderMapper;
import com.isariev.orderservice.eventHandlers.KafkaEventHandler;
import com.isariev.orderservice.model.Order;
import com.isariev.orderservice.model.OrderLineItems;
import com.isariev.orderservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final KafkaEventHandler eventHandler;
    private final static String INVENTORY_TOPIC = "inventory-topic";


    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder(final OrderRequest orderRequest, HttpServletRequest request) {
        Order order = createAndSaveOrder(orderRequest);
        List<String> skuCodes = extractSkuCodes(order);

        sendOrderResponse(order, skuCodes);
        sendOrderDetails(orderRequest, order.getId(), request);
    }

    private void sendOrderResponse(final Order order, final List<String> skuCodes) {
        OrderResponseDto responseDto = new OrderResponseDto(order.getId(), skuCodes);
        log.info("Send order response with id: {} -> to inventory service", responseDto.orderId());
        this.eventHandler.sendOrderResponse(responseDto);
    }

    private void sendOrderDetails(final OrderRequest orderRequest, final Long orderId, HttpServletRequest request) {
        OrderDetailsDto orderDetailsDto = createOrderDetailsDto(orderRequest, orderId, request);
        log.info("Send order details with id: {} and userId: {} -> to inventory service", orderDetailsDto.orderId(), orderDetailsDto.userId());
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
        log.info("Saving order with order number: {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    private OrderDetailsDto createOrderDetailsDto(final OrderRequest orderRequest, final Long orderId, HttpServletRequest request) {
        UserInfoDto userInfoDto = getUserInfo(request);

        return new OrderDetailsDto(
                orderRequest.getDeliveryInfo().deliveryCountry(),
                orderRequest.getDeliveryInfo().deliveryCity(),
                orderRequest.getDeliveryInfo().deliveryStreet(),
                orderRequest.getDeliveryInfo().deliveryAddress(),
                userInfoDto.sub(),
                userInfoDto.preferredUsername(),
                userInfoDto.email(),
                orderId
        );
    }

    private List<String> extractSkuCodes(final Order order) {
        log.info("Extract sku codes from order: {}", order.getId());
        return order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());
    }

    private UserInfoDto getUserInfo(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization").substring("Bearer ".length());

        return webClient.get()
                .uri("/userinfo")
                .headers(header -> header.setBearerAuth(bearer))
                .retrieve()
                .bodyToMono(UserInfoDto.class)
                .block();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = INVENTORY_TOPIC, groupId = "groupId")
    public void consumeSkuCodes(@Payload final Long orderId) {
        log.error("Order was failed with id: {}", orderId);
        orderRepository.updateStatusById("FAILED", orderId);
    }

}
