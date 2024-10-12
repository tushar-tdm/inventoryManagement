package com.intuit.inventory.management.models.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantityUpdateDTO {

    @NotNull
    private Integer productId;
    @NotNull
    private Integer shelfNumber;
    @NotNull
    private Integer vendorId;

    @Min(value = 0)
    private Integer quantity;
}
