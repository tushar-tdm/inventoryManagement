package com.intuit.inventory.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetails {

    @Id
    private Integer productId;
    private String productName;
    private String category;
    @Column(columnDefinition = "JSON")
    private String productDescription;
}
