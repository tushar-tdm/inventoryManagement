package com.intuit.inventory.management.models.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDTO {

    @NotNull
    @Min(value = 0)
    private Integer shelfNumber;

    @NotNull
    @Min(value = 0)
    private Integer quantity;

    @Min(value = 0)
    private Double price;

    @NotNull
    @NotBlank
    private String vendorLink;

    @NotNull
    @NotBlank
    private String productName;

    @NotNull
    private String category;

    private Object productDescription;
}