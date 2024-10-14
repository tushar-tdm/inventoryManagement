package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findByVendorLink(String vendorLink);
}
