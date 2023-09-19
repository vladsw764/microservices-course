package com.isariev.orderservice.service;

import com.isariev.orderservice.config.kafkaConfig.KafkaTopicConfig;
import com.isariev.orderservice.dto.OrderDetailsDto;
import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.dto.OrderResponseDto;
import com.isariev.orderservice.dto.UserInfoDto;
import com.isariev.orderservice.dto.mapper.OrderMapper;
import com.isariev.orderservice.model.Order;
import com.isariev.orderservice.model.OrderLineItems;
import com.isariev.orderservice.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, OrderResponseDto> kafkaTemplate;
    private final KafkaTemplate<String, OrderDetailsDto> orderInfoDtoKafkaTemplate;
    private final KafkaTopicConfig topicConfig;
    private static String CUSTOMER_ORDER_TOPIC;
    private static String INVENTORY_ORDER_TOPIC;

    @PostConstruct
    private void init() {
        CUSTOMER_ORDER_TOPIC = topicConfig.customerOrderTopic().name();
        INVENTORY_ORDER_TOPIC = topicConfig.inventoryOrderTopic().name();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void placeOrder(OrderRequest orderRequest, HttpServletRequest request) {
        Order order = createAndSaveOrder(orderRequest);
        List<String> skuCodes = extractSkuCodes(order);

        sendOrderResponse(order, skuCodes);
        sendOrderDetails(orderRequest, order.getId(), request);
    }

    private void sendOrderResponse(Order order, List<String> skuCodes) {
        OrderResponseDto responseDto = new OrderResponseDto(order.getId(), skuCodes);
        kafkaTemplate.send(INVENTORY_ORDER_TOPIC, responseDto);
    }

    private void sendOrderDetails(OrderRequest orderRequest, Long orderId, HttpServletRequest request) {
        OrderDetailsDto orderDetailsDto = createOrderDetailsDto(orderRequest, orderId, request);
        orderInfoDtoKafkaTemplate.send(CUSTOMER_ORDER_TOPIC, orderDetailsDto);
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

    private OrderDetailsDto createOrderDetailsDto(OrderRequest orderRequest, Long orderId, HttpServletRequest request) {
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

    private List<String> extractSkuCodes(Order order) {
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
    @KafkaListener(topics = "${spring.kafka.topics.order}", groupId = "groupId")
    public void consumeSkuCodes(Long orderId) {
        orderRepository.updateStatusById("FAILED", orderId);
    }
}
