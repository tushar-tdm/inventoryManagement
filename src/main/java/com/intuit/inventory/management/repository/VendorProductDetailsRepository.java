package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.VendorProductDetails;
import com.intuit.inventory.management.entity.VendorProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VendorProductDetailsRepository extends JpaRepository<VendorProductDetails, VendorProductKey> {

    // Modifying annotation should be used when we are writing custom DELETE, INSERT, UPDATE commands
    // Or else we will get "JDBC exception executing SQL [<Our custom SQL command>] [No results were returned by the query.] [n/a]",
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM vendor_product_details v WHERE v.product_id = :productId")
    void deleteByProductId(Integer productId);

    @Query(nativeQuery = true, value = "SELECT * FROM vendor_product_details v WHERE v.vendor_id = :vendorId")
    List<VendorProductDetails> findAllByVendorId(Integer vendorId);

    @Query(nativeQuery = true, value = "SELECT v.vendor_id FROM vendor_product_details v WHERE v.product_id = :productId")
    List<Integer> findAllByProductId(Integer productId);

    @Query(nativeQuery = true, value = "SELECT COUNT(product_id) FROM vendor_product_details WHERE vendor_id = :vendorId")
    int countProductsByVendorId(Integer vendorId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM vendor_product_details v WHERE v.vendor_id = :vendorId")
    void deleteByVendorId(Integer vendorId);

    @Query(nativeQuery = true, value = "SELECT v.product_id FROM vendor_product_details v WHERE v.vendor_id = :vendorId")
    List<Integer> getProductIdByVendorId(Integer vendorId);
}
