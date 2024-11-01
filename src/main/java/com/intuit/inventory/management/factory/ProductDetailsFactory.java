package com.intuit.inventory.management.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductDescriptionOrCategory;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

public class ProductDetailsFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsFactory.class);

    public static ProductDetails createOrFetchProductDetails(ProductCreateRequestDTO productRequest, ProductDetailRepository productDetailRepository)
            throws AddingProductWithoutProductDescriptionOrCategory {

        List<ProductDetails> existingProductDetails = productDetailRepository.findByProductNameAndCategory(productRequest.getProductName(), productRequest.getCategory());
        ProductDetails productDetails = new ProductDetails();
        boolean foundProductDetail = false;

        if (existingProductDetails.size() > 0) {
            // New product is being added, product description and category are needed
            for (ProductDetails productDetailsListItem : existingProductDetails) {
                if (productDetailsListItem.getProductDescription() != null &&
                        productRequest.getProductDescription() != null &&
                        productDetailsListItem.getProductDescription().equalsIgnoreCase(productRequest.getProductDescription().toString())
                ) {
                    productDetails = productDetailsListItem;
                    foundProductDetail = true;
                    break;
                }
            }
        }

        if (existingProductDetails.size() == 0 || !foundProductDetail) {
            if (productRequest.getProductDescription() == null || productRequest.getCategory() == null) {
                throw new AddingProductWithoutProductDescriptionOrCategory("Product Description and category should be given while adding a new product.");
            } else {
                productDetails = createProductDetails(productRequest, productDetailRepository);
            }
        }

        return productDetails;
    }

    public static ProductDetails createProductDetails(ProductCreateRequestDTO productRequest, ProductDetailRepository productDetailRepository) {
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
        return productDetailRepository.save(newProductDetails);
    }
}
