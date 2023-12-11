package com.isariev.orderservice.controller;

import com.isariev.orderservice.dto.OrderRequest;
import com.isariev.orderservice.service.OrderService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @PostMapping
    @Secured("ROLE_CLIENT")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        LOGGER.info("Attempting to place an order.");
        orderService.placeOrder(orderRequest);
        return ResponseEntity.ok("order placed successfully");
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/userRole")
    public String getUserClaims(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String[] headerParts = authorizationHeader.split(" ");
        if (headerParts.length == 2 && headerParts[0].equalsIgnoreCase("Bearer")) {
            String token = headerParts[1];
            Map<String, Object> userRole = extractUserSubFromToken(token);
            return "User claims are: " + userRole.toString();
        } else {
            return "Invalid Authorization header format";
        }
    }

    public Map<String, Object> extractUserSubFromToken(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getClaims();
        } catch (ParseException e) {
            throw new JwtException("Error extracting user ID from token", e);
        }
    }
}