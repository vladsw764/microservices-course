package com.isariev.customerservice.controller;

import com.isariev.customerservice.dto.OrderInfoDto;
import com.isariev.customerservice.service.CustomerServiceImpl;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

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
    public Integer getOrdersInMonth(){
        return customerService.getOrdersInMonth();
    }
}
