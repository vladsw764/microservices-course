package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_inventory")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public final class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
