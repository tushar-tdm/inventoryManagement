package com.intuit.inventory.management.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductNameOrCategory;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

public class ProductDetailsFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsFactory.class);

    public static ProductDetails createOrFetchProductDetails(ProductCreateRequestDTO productRequest, ProductDetailRepository productDetailRepository)
            throws AddingProductWithoutProductNameOrCategory {
        Optional<ProductDetails> existingProductDetails = productDetailRepository.findByProductName(productRequest.getProductName());
        ProductDetails productDetails = new ProductDetails();

        if (existingProductDetails.isEmpty()) {
            // New product is being added, product description and category are needed
            if (productRequest.getProductName() == null || productRequest.getCategory() == null) {
                throw new AddingProductWithoutProductNameOrCategory("Product name and category should be given while adding a new product.");
            } else {
                ProductDetails newProductDetails = new ProductDetails();
                newProductDetails.setProductName(productRequest.getProductName());
                newProductDetails.setCategory(productRequest.getCategory());
                try {
                    String productDescriptionJson = objectMapper.writeValueAsString(productRequest.getProductDescription());
                    newProductDetails.setProductDescription(productDescriptionJson);
                } catch (JsonProcessingException e) {
                    logger.error("Error Parsing Json to String");
                    throw new RuntimeException("Error converting JSON to String", e);
                }
                productDetails = productDetailRepository.save(newProductDetails);
            }
        } else {
            productDetails = existingProductDetails.get();
        }

        return productDetails;
    }
}
