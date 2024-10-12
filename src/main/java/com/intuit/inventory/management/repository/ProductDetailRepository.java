package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetails, Integer> {

}
