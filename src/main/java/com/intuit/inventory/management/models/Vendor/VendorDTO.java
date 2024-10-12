package com.intuit.inventory.management.models.Vendor;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorDTO {
    @NotNull
    private Integer vendorId;
    private String vendorLink;
}
