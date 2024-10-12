package com.intuit.inventory.management.models.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product.
 * This will be used to add a new product to inventory.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDTO {
    @NotNull
    private Integer productId;
    @NotNull
    @Min(value = 0)
    private Integer shelfNumber;
    @NotNull
    private Integer vendorId;
    @NotNull
    @Min(value = 0)
    private Integer quantity;
    @NotNull
    @Min(value = 0)
    private Double price;
//    @NotNull
    @NotBlank
    private String vendorLink;
//    @NotNull
    @NotBlank
    private String productName;
//    @NotNull
    @NotBlank
    private String category;

    private Object productDescription;
}