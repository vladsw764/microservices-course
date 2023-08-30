package com.isariev.orderservice.controller;

import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @CircuitBreaker(name = "inventory")
//    @CircuitBreaker(name = "InventoryServiceBasedOnTime")
//    @TimeLimiter(name = "inventory")
//    @Retry(name = "inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }
//
//    public CompletableFuture<String> placeOrderFallback(OrderRequest orderRequest, RuntimeException e) {
//        return CompletableFuture.supplyAsync(() -> "Oops! Sth went wrong, please try to order later!");
//    }
}

//, fallbackMethod = "placeOrderFallback"
