package com.isariev.customerservice.model;

import lombok.*;
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
                ", username='" + userId + '\'' +
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
