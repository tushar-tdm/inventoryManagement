package com.intuit.inventory.management.models.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponseDTO {
    private Integer redMin;
    private Integer yellowMin;
    private Integer greenMin;
    private List<ProductInformationDTO> products;
}
