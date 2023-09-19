package com.isariev.customerservice.controller;

import com.isariev.customerservice.dto.OrderDiscountDto;
import com.isariev.customerservice.dto.OrderInfoDto;
import com.isariev.customerservice.service.CustomerServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerServiceImpl customerService;

    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public OrderInfoDto getCountOfOrdersById(@RequestParam("customerId") String customerId) {
        return customerService.getCountOfOrdersById(customerId);
    }

    @GetMapping("/count")
    public OrderInfoDto getCountOfOrders() {
        return customerService.getCountOfOrders();
    }

    @GetMapping("/discount")
    public OrderDiscountDto getOrdersInMonth() {
        return customerService.getOrdersInMonth();
    }
}
