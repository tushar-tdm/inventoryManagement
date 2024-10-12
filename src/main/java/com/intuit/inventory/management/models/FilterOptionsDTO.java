package com.intuit.inventory.management.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterOptionsDTO {
    private Integer minPrice;
    private Integer maxPrice;
    @NotBlank
    private String category;
    @NotBlank
    private String vendorLink;
}
