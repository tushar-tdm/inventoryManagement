package com.intuit.inventory.management.models.Vendor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorProductUpdateDTO {

    @NotNull
    private Integer vendorId;
    @NotNull
    private Integer productId;

    @Min(value = 0)
    private Double price;
}
