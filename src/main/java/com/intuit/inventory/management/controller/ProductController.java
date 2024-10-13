package com.intuit.inventory.management.controller;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.exceptions.AddingAnExistingProductException;
import com.intuit.inventory.management.exceptions.AddingNewVendorWithoutVendorLinkException;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductNameOrCategory;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.models.product.*;
import com.intuit.inventory.management.service.NotificationService;
import com.intuit.inventory.management.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private NotificationService notificationService;

    @Value("${page.size}")
    private int pageSize;

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    /** ****************************************************
     * GET CALLS
     * ****************************************************
     */

    @GetMapping(value = "/list")
    public ResponseEntity<ProductListResponseDTO> getAllProducts() {
        logger.info("Getting all of the products");
        ProductListResponseDTO products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/list/page/{pageNo}")
    public ResponseEntity<ProductListPagedResponseDTO> getAllProductsPaged(@PathVariable Integer pageNo) {
        logger.info("Getting all of the products in pagination format");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        ProductListPagedResponseDTO products = productService.getAllProductsPaged(pageable);
        return ResponseEntity.ok(products);
    }

    /** ****************************************************
     * POST CALLS
     * ****************************************************
     */

    @PostMapping(value = "/save",consumes = "application/json")
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody ProductCreateRequestDTO product) throws AddingAnExistingProductException, AddingNewVendorWithoutVendorLinkException, AddingProductWithoutProductNameOrCategory {
        logger.info("Adding a product to inventory. Product Id: " + product.getProductId());
        Product savedProduct = productService.registerProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    /** ****************************************************
     * DELETE CALLS
     * ****************************************************
     */

    @DeleteMapping(value = "/delete/{productId}/shelf/{shelfNumber}/vendor/{vendorId}")
    public ResponseEntity<String> deleteProductByVendorInShelf(
            @PathVariable Integer productId,
            @PathVariable Integer shelfNumber,
            @PathVariable Integer vendorId
    ) throws ProductNotFoundException {
        logger.info("Deleting a product from the inventory. Product Id: " + productId);
        productService.deleteProductByIdShelfNumberAndVendorId(productId, shelfNumber, vendorId);
        return ResponseEntity.ok("Product with productId: " + productId +
                " and shelf number: " + shelfNumber + " deleted successfully");
    }

    @DeleteMapping(value = "/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable Integer productId) {
        logger.info("Deleting a product from the inventory. Product Id: " + productId);
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product with productId: " + productId + " deleted successfully");
    }

    /** ****************************************************
     * PUT CALLS
     * ****************************************************
     */

    @PutMapping(value = "/updateQuantity")
    public ResponseEntity<Product> updateProductQuantityByVendorInShelf(@Valid @RequestBody ProductQuantityUpdateDTO product) throws ProductNotFoundException {
        logger.info("Updating the product: " + product.getProductId() + ", in shelf: " + product.getShelfNumber());
        Product updatedProduct = productService.updateProductQuantity(product);
        return ResponseEntity.ok(updatedProduct);
    }

}
