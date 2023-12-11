package com.isariev.customerservice.service;

import com.isariev.customerservice.dto.OrderDetailsDto;
import com.isariev.customerservice.dto.OrderDiscountDto;
import com.isariev.customerservice.dto.OrderInfoDto;
import com.isariev.customerservice.dto.mapper.CustomerMapper;
import com.isariev.customerservice.model.Customer;
import com.isariev.customerservice.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl {

    private final static String ORDER_TOPIC = "order-topic";

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final HttpServletRequest request;

    private final WebClient webClient;

    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, HttpServletRequest request, WebClient webClient, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.request = request;
        this.webClient = webClient;
        this.customerMapper = customerMapper;
    }

    public OrderInfoDto getCountOfOrdersById(String customerId) {
        int count = customerRepository.findAllByCustomerId(customerId).size();

        return new OrderInfoDto(
                count,
                customerId,
                "Text"
        );
    }

    public OrderInfoDto getCountOfOrders() {
        String customerId = getCustomerId();

        int count = customerRepository.findAllByCustomerId(customerId).size();

        return new OrderInfoDto(
                count,
                customerId,
                "Text"
        );
    }

    public OrderDiscountDto getOrdersInMonth() {
        var customersId = customerRepository.findAll().stream().map(Customer::getUserId).toList();
        List<OrderInfoDto> ordersInfo = new ArrayList<>();
        for (var id : customersId) {
            ordersInfo.add(getCountOfOrdersById(id));
        }
        int count = ordersInfo.stream().filter(orderInfoDto -> orderInfoDto.count() >= 2).toList().size();
        return new OrderDiscountDto(count, "In next month you have discount 50$");
    }

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = ORDER_TOPIC,
            groupId = "groupId",
            containerFactory = "factory",
            topicPartitions = @TopicPartition(topic = ORDER_TOPIC, partitions = {"2"})
    )
    public void saveCustomerDetails(@Payload OrderDetailsDto orderDetails) {
        Customer savedCustomer = customerRepository.save(customerMapper.mapToEntity(orderDetails));
        LOGGER.info("customer saved with id: {}", savedCustomer.getId());
        LOGGER.info("customer details: {}, {}", savedCustomer.getDeliveryStreet(), savedCustomer.getDeliveryAddress());
    }

    private String getCustomerId() {
        String bearer = request.getHeader("Authorization").substring("Bearer ".length());
        return webClient.get()
                .uri("/userinfo/id")
                .headers(header -> header.setBearerAuth(bearer))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

