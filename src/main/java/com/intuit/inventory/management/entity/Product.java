package com.intuit.inventory.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(ProductKey.class)
public class Product {
    @Id
    private Integer productId;
    @Id
    private Integer shelfNumber;
    @Id
    private Integer vendorId;

    @PositiveOrZero(message = "Product quantity cannot be less than 0")
    @NotNull
    private Integer quantity;

    // To get the details of the product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", referencedColumnName = "productId", insertable = false, updatable = false)
    private ProductDetails productDetails;

    // To get the price set by each vendor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendorId", referencedColumnName = "vendorId", insertable = false, updatable = false)
    @JoinColumn(name = "productId", referencedColumnName = "productId", insertable = false, updatable = false)
    private VendorProductDetails vendorProductDetails;

}
