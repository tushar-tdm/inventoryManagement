package com.intuit.inventory.management.models.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {
    @NotNull
    private Integer productId;
    @NotBlank
    private String productName;
    @NotBlank
    private String productDescription;
    @NotBlank
    private String category;
}