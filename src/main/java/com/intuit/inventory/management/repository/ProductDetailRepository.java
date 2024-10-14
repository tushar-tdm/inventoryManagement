package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetails, Integer> {

    @Query(nativeQuery = true, value = "Select * from product_details pd WHERE pd.product_name = :productName")
    Optional<ProductDetails> findByProductName(String productName);

    Optional<ProductDetails> findByProductId(Integer productId);
}
