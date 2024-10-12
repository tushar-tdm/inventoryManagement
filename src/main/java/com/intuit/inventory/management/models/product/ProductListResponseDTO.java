package com.intuit.inventory.management.models.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseDTO {
    private Integer redMin;
    private Integer yellowMin;
    private Integer greenMin;
    private List<ProductInformationDTO> products;
}
