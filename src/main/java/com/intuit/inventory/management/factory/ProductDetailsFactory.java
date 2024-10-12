package com.intuit.inventory.management.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductNameOrCategory;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

public class ProductDetailsFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ProductDetails createOrFetchProductDetails(ProductCreateRequestDTO productRequest, ProductDetailRepository productDetailRepository)
            throws AddingProductWithoutProductNameOrCategory {
        Optional<ProductDetails> productDetailsOptional = productDetailRepository.findById(productRequest.getProductId());
        ProductDetails productDetails = new ProductDetails();
        if (productDetailsOptional.isEmpty()) {
            // New product is being added, product description and category are needed
            if (productRequest.getProductName() == null || productRequest.getCategory() == null) {
                throw new AddingProductWithoutProductNameOrCategory("Please provide product name and category should be given while adding a new product");
            } else {
                ProductDetails newProductDetails = new ProductDetails();
                newProductDetails.setProductId(productRequest.getProductId());
                newProductDetails.setProductName(productRequest.getProductName());
                newProductDetails.setCategory(productRequest.getCategory());
                try {
                    String productDescriptionJson = objectMapper.writeValueAsString(productRequest.getProductDescription());
                    newProductDetails.setProductDescription(productDescriptionJson);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error converting product description to JSON", e);
                }
                productDetails = productDetailRepository.save(newProductDetails);
            }
        } else {
            productDetails = productDetailsOptional.get();
        }

        return productDetails;
    }
}
