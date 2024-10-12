package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Vendor findVendorIdByVendorLink(String vendorLink);
}
