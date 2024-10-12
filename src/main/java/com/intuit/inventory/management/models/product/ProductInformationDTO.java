package com.intuit.inventory.management.models.product;

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
public class ProductInformationDTO {
    private String productName;
    private String category;
    private String productDescription;
    private Integer quantity;
    private Double price;
    private String vendorLink;
    private Integer shelfNumber;
    private Integer productId;
    private Integer vendorId;

}
