package com.intuit.inventory.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.*;
import com.intuit.inventory.management.exceptions.AddingAnExistingProductException;
import com.intuit.inventory.management.exceptions.AddingNewVendorWithoutVendorLinkException;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductNameOrCategory;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.factory.ProductDetailsFactory;
import com.intuit.inventory.management.factory.VendorFactory;
import com.intuit.inventory.management.factory.VendorProductDetailsFactory;
import com.intuit.inventory.management.models.product.*;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import com.intuit.inventory.management.repository.ProductRepository;
import com.intuit.inventory.management.repository.VendorProductDetailsRepository;
import com.intuit.inventory.management.repository.VendorRepository;
import com.intuit.inventory.management.strategy.implementation.ProductFetchStrategy;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailsRepository;

    @Autowired
    private VendorProductDetailsRepository vendorProductDetailsRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Value("${red.color.code.min}")
    private Integer redColorCodeMin;

    @Value("${yellow.color.code.min}")
    private Integer yellowColorCodeMin;

    @Value("${green.color.code.min}")
    private Integer greenColorCodeMin;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductFetchStrategy fetchStrategy;

    public ProductListResponseDTO getAllProducts() {
        List<ProductInformationDTO> productInformationDTOList = fetchStrategy.fetchAll();

        return new ProductListResponseDTO(
            redColorCodeMin,
            yellowColorCodeMin,
            greenColorCodeMin,
            productInformationDTOList
        );
    }

    public ProductListResponseDTO getAllProductsPaged(Pageable pageable) {
        List<ProductInformationDTO> productInformationDTOList = fetchStrategy.fetchPaged(pageable);

        return new ProductListResponseDTO(
                redColorCodeMin,
                yellowColorCodeMin,
                greenColorCodeMin,
                productInformationDTOList
        );
    }

    @Transactional
    public Product registerProduct(ProductCreateRequestDTO productRequest) throws AddingProductWithoutProductNameOrCategory, AddingNewVendorWithoutVendorLinkException, AddingAnExistingProductException {

        // Check if the product by this vendor already exists in the given shelf
        ProductKey productKey = new ProductKey(productRequest.getProductId(), productRequest.getShelfNumber(), productRequest.getVendorId());
        if (productRepository.findById(productKey).isPresent()) {
            throw new AddingAnExistingProductException("This product supplied by vendor" + productRequest.getVendorId() +
                    "is already available at the shelf " + productRequest.getShelfNumber() +
                    ". Please update the quantity instead");
        }

        // Get productDetails object from the factory
        ProductDetails productDetails = ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailsRepository);

        // Create/Get the vendor details
        Vendor vendor = VendorFactory.createOrFetchVendor(productRequest, vendorRepository);
        VendorProductKey vendorProductKey = new VendorProductKey(productRequest.getVendorId(), productRequest.getProductId());

        VendorProductDetails vendorProductDetails = VendorProductDetailsFactory.createOrFetchVendorProductDetails(
                vendorProductKey,
                vendorProductDetailsRepository,
                productRequest,
                vendor
                );

        // Create and save Product
        Product product = new Product();
        product.setShelfNumber(productRequest.getShelfNumber());
        product.setProductId(productRequest.getProductId());
        product.setVendorId(productRequest.getVendorId());
        product.setQuantity(productRequest.getQuantity());
        product.setVendorProductDetails(vendorProductDetails);
        product.setProductDetails(productDetails); // Link to ProductDetails

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProductByIdShelfNumberAndVendorId(Integer productId, Integer shelfNumber, Integer vendorId)
            throws ProductNotFoundException {
        ProductKey productKey = new ProductKey(productId, shelfNumber, vendorId);
        VendorProductKey vendorProductKey = new VendorProductKey(vendorId, productId);

        if (productRepository.existsById(productKey)) {
            // Remove from the main products table
            productRepository.deleteById(productKey);

            // If the product has been removed completely, remove its details from other tables too
            if (productRepository.findByProductId(productId).isEmpty()) {
                // delete the corresponding ProductDetails and VendorProductDetails too
                productDetailsRepository.deleteById(productId);
                vendorProductDetailsRepository.deleteByProductId(productId);
                // check if the vendor was supplying only this product
                if (vendorProductDetailsRepository.findAllByVendorId(vendorId).isEmpty() ) {
                    // remove the vendor
                    vendorRepository.deleteById(vendorId);
                }
            }
            // Make sure that this vendor doesn't have his products in other shelves.
            // If he doesn't have this product in other shelves, then we can remove this product from this vendor
            else if (productRepository.findShelfNumbersByProductIdAndVendorId(vendorId, productId).isEmpty()) {
                vendorProductDetailsRepository.deleteById(vendorProductKey);
                if (vendorProductDetailsRepository.findAllByVendorId(vendorId).isEmpty()) {
                    vendorRepository.deleteById(vendorId);
                }
            }
        } else {
            throw new ProductNotFoundException("Product not found with productId: " + productId + " and shelfNumber: " + shelfNumber);
        }
    }

    // Delete the whole product by the product Id
    @Transactional
    public void deleteProduct(Integer productId) {
        productRepository.deleteByProductId(productId);
        productDetailsRepository.deleteById(productId);
        // get all the vendors who were selling this product
        List<Integer> vendors = vendorProductDetailsRepository.findAllByProductId(productId);
        List<Integer> vendorIdsToBeDeleted = vendors.stream()
                .filter(vendorId -> vendorProductDetailsRepository.countProductsByVendorId(vendorId) == 1)
                .collect(Collectors.toList());
        vendorProductDetailsRepository.deleteByProductId(productId);
        if (!vendorIdsToBeDeleted.isEmpty()) {
            vendorRepository.deleteAllById(vendorIdsToBeDeleted);
        }
    }

    @Transactional
    public Product updateProductQuantity(ProductQuantityUpdateDTO product) throws ProductNotFoundException {
        ProductKey productKey = new ProductKey(product.getProductId(), product.getShelfNumber(), product.getVendorId());
        Optional<Product> optionalProduct = productRepository.findById(productKey);

        // Check if the product exists
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found for productId: " + product.getProductId() +
                    " in shelf: " + product.getShelfNumber() + " by vendor: " + product.getVendorId());
        }

        // Get the existing product to update fields
        Product existingProduct = optionalProduct.get();

        // Update fields based on incoming DTO
        existingProduct.setQuantity(product.getQuantity());

        // Retrieve vendor product details and product details only if needed
        VendorProductKey vendorProductKey = new VendorProductKey(product.getVendorId(), product.getProductId());
        vendorProductDetailsRepository.findById(vendorProductKey)
                .ifPresent(existingProduct::setVendorProductDetails);

        productDetailsRepository.findById(product.getProductId())
                .ifPresent(existingProduct::setProductDetails);

        // Save and return the updated product
        return productRepository.save(existingProduct);
    }

}
