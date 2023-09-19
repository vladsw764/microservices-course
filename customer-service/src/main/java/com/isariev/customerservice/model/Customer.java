package com.isariev.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "customers")
public class Customer {

    @Id
    private String id;

    private String userId;

    private String username;

    private String email;

    private String deliveryCountry;

    private String deliveryCity;

    private String deliveryStreet;

    private String deliveryAddress;

    private Long orderId;

    @CreatedDate
    private LocalDateTime orderDate;


    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", deliveryCountry='" + deliveryCountry + '\'' +
                ", deliveryCity='" + deliveryCity + '\'' +
                ", deliveryStreet='" + deliveryStreet + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", orderId=" + orderId +
                ", orderDate=" + orderDate +
                '}';
    }

    public void setOrderDate() {
        this.orderDate = LocalDateTime.now();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDeliveryCountry(String deliveryCountry) {
        this.deliveryCountry = deliveryCountry;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }

    public void setDeliveryStreet(String deliveryStreet) {
        this.deliveryStreet = deliveryStreet;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeliveryCountry() {
        return deliveryCountry;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public String getDeliveryStreet() {
        return deliveryStreet;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
