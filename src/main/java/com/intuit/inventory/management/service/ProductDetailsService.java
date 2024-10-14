package com.intuit.inventory.management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.models.product.ProductUpdateRequestDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductDetailsService {

    @Autowired
    ProductDetailRepository productDetailsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    public ProductDetails getProductDetailByProductId(ProductCreateRequestDTO productRequest) {
//        ProductDetails productDetail= productDetailsRepository.findById(productRequest.getProductId())
//                .orElseGet(() -> {
//                    ProductDetails newProductDetails = new ProductDetails();
//                    newProductDetails.setProductId(productRequest.getProductId());
//                    newProductDetails.setProductName(productRequest.getProductName());
//                    newProductDetails.setCategory(productRequest.getCategory());
//                    try {
//                        String productDescriptionJson = objectMapper.writeValueAsString(productRequest.getProductDescription());
//                        newProductDetails.setProductDescription(productDescriptionJson);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException("Error converting product description to JSON", e);
//                    }
//                    return productDetailsRepository.save(newProductDetails);
//                });
//        return productDetail;
//    }

    public ProductDetails findById(Integer productId) throws ProductNotFoundException {
        Optional<ProductDetails> productDetails = productDetailsRepository.findById(productId);
        if (productDetails.isPresent()){
            return productDetails.get();
        }
        else {
            throw new ProductNotFoundException("Product with id: " + productId + " was not found");
        }
    }

    public ProductDetails updateProductDetail(ProductUpdateRequestDTO request) throws ProductNotFoundException {

        Optional<ProductDetails> productDetailOptional = productDetailsRepository.findById(request.getProductId());
        if (productDetailOptional.isEmpty()) {
            throw new ProductNotFoundException("Product not found for productId: " + request.getProductId());
        }

        ProductDetails productDetails = productDetailOptional.get();
        // Name
        if (request.getProductName() != null) {
            productDetails.setProductName(request.getProductName());
        }
        // Description
        if (request.getProductDescription() != null) {
            productDetails.setProductDescription(request.getProductDescription());
        }
        // Category
        if (request.getCategory() != null) {
            productDetails.setCategory(request.getCategory());
        }

        productDetails.setProductId(request.getProductId());
        // save and return the updated product
        return productDetailsRepository.save(productDetails);
    }
}
