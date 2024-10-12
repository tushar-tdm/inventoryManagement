package com.intuit.inventory.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VendorProductKey.class)
public class VendorProductDetails {
    @Id
    private Integer vendorId;
    @Id
    private Integer productId;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "vendorId", referencedColumnName = "vendorId", insertable = false, updatable = false)
    private Vendor vendor;

}
