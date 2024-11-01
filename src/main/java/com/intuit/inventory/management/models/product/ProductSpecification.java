package com.intuit.inventory.management.models.product;

import com.intuit.inventory.management.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public Specification<Product> getProductsByFilter(ProductFilterConditionsDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("productDetails").get("productName")), "%" + filter.getProductName().toLowerCase() + "%"));
            }

            if (filter.getVendorLink() != null && !filter.getVendorLink().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("vendorProductDetails").get("vendorLink")), "%" + filter.getVendorLink().toLowerCase() + "%"));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("vendorProductDetails").get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("vendorProductDetails").get("price"), filter.getMaxPrice()));
            }

            if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("productDetails").get("category")), filter.getCategory().toLowerCase()));
            }

            query.orderBy(criteriaBuilder.asc(root.get("productDetails").get("productName")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
