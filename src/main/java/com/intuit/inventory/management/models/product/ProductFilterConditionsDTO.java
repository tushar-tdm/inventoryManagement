package com.intuit.inventory.management.models.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterConditionsDTO {
    @NotBlank
    private String productName;
    private String vendorLink;
    private Double minPrice;
    private Double maxPrice;
    private String category;
}
