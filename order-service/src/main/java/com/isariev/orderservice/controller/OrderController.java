package com.isariev.orderservice.controller;

import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @CircuitBreaker(name = "inventory")
//    @CircuitBreaker(name = "InventoryServiceBasedOnTime")
    @TimeLimiter(name = "inventoryTimelimiter")
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Attempting to place an order.");
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatusCode.valueOf(201)));
    }
}

//    @Retry(name = "inventory")
