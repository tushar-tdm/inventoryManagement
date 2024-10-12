package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductKey> {

    Optional<Product> findByProductId(Integer productId);

    void deleteByProductId(Integer productId);

    @Query(nativeQuery = true, value = "SELECT p.shelf_number FROM product p WHERE p.product_id = :productId AND p.vendor_id = :vendorId")
    List<Integer> findShelfNumbersByProductIdAndVendorId(Integer vendorId, Integer productId);

    void deleteByVendorId(Integer vendorId);

    Page<Product> findAll(Pageable pageable);
}
