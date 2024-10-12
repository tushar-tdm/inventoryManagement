package com.intuit.inventory.management.controller;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.models.product.ProductUpdateRequestDTO;
import com.intuit.inventory.management.service.ProductDetailsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/productDetail")
@RestController
public class ProductDetailsController {

    @Autowired
    ProductDetailsService productDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsController.class);

    @GetMapping(value = "/getProduct/{productId}")
    public ResponseEntity<ProductDetails> getProductDetailsByProductId(@PathVariable Integer productId) throws ProductNotFoundException {
        logger.info("Fetching a product by the ID: " + productId);
        ProductDetails productDetails = productDetailsService.findById(productId);
        return ResponseEntity.ok(productDetails);
    }

    @PutMapping(value = "/updateProduct")
    public ResponseEntity<ProductDetails> updateProduct(@Valid @RequestBody ProductUpdateRequestDTO product) throws ProductNotFoundException {
        logger.info("Updating the product: " + product.getProductId());
        ProductDetails updatedProductDetails = productDetailsService.updateProductDetail(product);
        return ResponseEntity.ok(updatedProductDetails);
    }
}
