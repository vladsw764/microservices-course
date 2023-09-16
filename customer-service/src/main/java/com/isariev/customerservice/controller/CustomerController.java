package com.isariev.customerservice.controller;

import com.isariev.customerservice.dto.OrderDiscountDto;
import com.isariev.customerservice.service.CustomerServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerServiceImpl customerService;

    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

//    @GetMapping
//    public OrderInfoDto getCountOfOrders(@RequestParam("customerId") String customerId) {
//        return customerService.getCountOfOrdersById(customerId);
//    }

    @GetMapping()
    public OrderDiscountDto getOrdersInMonth() {
        return customerService.getOrdersInMonth();
    }
}
