package com.isariev.customerservice.service;

import com.isariev.customerservice.dto.OrderDetailsDto;
import com.isariev.customerservice.dto.OrderDiscountDto;
import com.isariev.customerservice.dto.OrderInfoDto;
import com.isariev.customerservice.dto.mapper.CustomerMapper;
import com.isariev.customerservice.model.Customer;
import com.isariev.customerservice.repository.CustomerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl {

    private final static String TOPIC_NEW = "customer-order-topic";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
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

    public OrderDiscountDto getOrdersInMonth() {
        var customersId = customerRepository.findAll().stream().map(Customer::getUserId).toList();
        List<OrderInfoDto> ordersInfo = new ArrayList<>();
        for (var id : customersId) {
            ordersInfo.add(getCountOfOrdersById(id));
        }
        int count = ordersInfo.stream().filter(orderInfoDto -> orderInfoDto.count() >= 2).toList().size();
        return new OrderDiscountDto(count, "In next month you have discount 50$");
    }

    @KafkaListener(topics = TOPIC_NEW, groupId = "groupId", containerFactory = "factory")
    public void saveCustomerDetails(OrderDetailsDto orderDetails) {
        Customer customer = customerMapper.mapToEntity(orderDetails);
        customerRepository.save(customer);
        System.out.println(customer);
    }
}

