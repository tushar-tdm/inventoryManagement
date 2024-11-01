package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetails, Integer> {

    @Query(nativeQuery = true, value = "Select * from product_details pd WHERE pd.product_name = :productName")
    Optional<ProductDetails> findByProductName(String productName);

    Optional<ProductDetails> findByProductId(Integer productId);

    void deleteByProductName(String productName);

    @Query(nativeQuery = true, value = "Select * from product_details pd WHERE pd.product_name = :productName AND pd.category = :category")
    List<ProductDetails> findByProductNameAndCategory(String productName, String category);

//    @Query(nativeQuery = true, value = "Select * from product_details pd WHERE pd.product_name = :productName AND pd.category = :category AND pd.product_description = :description")
//    List<ProductDetails> findByProductNameAndCategoryAndDescription(String productName, String category, String description);
}
