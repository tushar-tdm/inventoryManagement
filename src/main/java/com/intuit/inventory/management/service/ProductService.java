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
import com.intuit.inventory.management.utils.MetricsUtil;
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

    @Autowired
    private MetricsUtil metricsUtil;

    @Value("${red.color.code.min}")
    private Integer redColorCodeMin;

    @Value("${yellow.color.code.min}")
    private Integer yellowColorCodeMin;

    @Value("${green.color.code.min}")
    private Integer greenColorCodeMin;

    @Value("${page.size}")
    private Integer pageSize;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductFetchStrategy fetchStrategy;

    public ProductListResponseDTO getAllProducts() {
        // Record the time for metrics
        long startTime = System.nanoTime();

        List<ProductInformationDTO> productInformationDTOList = fetchStrategy.fetchAll();

        // Update the metrics
        long duration = System.nanoTime() - startTime;
        metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.GET_ALL_PRODUCTS, duration);
        metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.GET_ALL_PRODUCTS);

        return new ProductListResponseDTO(
            redColorCodeMin,
            yellowColorCodeMin,
            greenColorCodeMin,
            productInformationDTOList
        );
    }

    public ProductListPagedResponseDTO getAllProductsPaged(Pageable pageable) {
        // Record the time for metrics
        long startTime = System.nanoTime();

        List<ProductInformationDTO> productInformationDTOList = fetchStrategy.fetchPaged(pageable);

        // Update the metrics
        long duration = System.nanoTime() - startTime;
        metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.GET_ALL_PRODUCTS_PAGED, duration);
        metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.GET_ALL_PRODUCTS_PAGED);
        return new ProductListPagedResponseDTO(
                redColorCodeMin,
                yellowColorCodeMin,
                greenColorCodeMin,
                pageSize,
                productInformationDTOList
        );
    }

    @Transactional
    public Product registerProduct(ProductCreateRequestDTO productRequest) throws AddingProductWithoutProductNameOrCategory, AddingNewVendorWithoutVendorLinkException, AddingAnExistingProductException {

        // Record the time for metrics
        long startTime = System.nanoTime();

        // Get productDetails object from the factory
        ProductDetails productDetails = ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailsRepository);
        // Create/Get the vendor details
        Vendor vendor = VendorFactory.createOrFetchVendor(productRequest, vendorRepository);

        // Check if the product by this vendor already exists in the given shelf
        ProductKey productKey = new ProductKey(productDetails.getProductId(), productRequest.getShelfNumber(), vendor.getVendorId());
        if (productRepository.findById(productKey).isPresent()) {
            throw new AddingAnExistingProductException("This product supplied by vendor" + productRequest.getVendorLink() +
                    "is already available at the shelf " + productRequest.getShelfNumber() +
                    ". Please update the quantity instead");
        }

        VendorProductKey vendorProductKey = new VendorProductKey(vendor.getVendorId(), productDetails.getProductId());

        VendorProductDetails vendorProductDetails = VendorProductDetailsFactory.createOrFetchVendorProductDetails(
                vendorProductKey,
                vendorProductDetailsRepository,
                productRequest,
                productDetails.getProductId(),
                vendor.getVendorId(),
                vendor
                );

        // Create and save Product
        Product product = new Product();
        product.setShelfNumber(productRequest.getShelfNumber());
        product.setProductId(productDetails.getProductId());
        product.setVendorId(vendor.getVendorId());
        product.setQuantity(productRequest.getQuantity());
        product.setVendorProductDetails(vendorProductDetails);
        product.setProductDetails(productDetails); // Link to ProductDetails

        // Update the metrics
        long duration = System.nanoTime() - startTime;
        metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.SAVE_PRODUCT, duration);
        metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.SAVE_PRODUCT);

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProductByIdShelfNumberAndVendorId(Integer productId, Integer shelfNumber, Integer vendorId)
            throws ProductNotFoundException {

        // Record the time for metrics
        long startTime = System.nanoTime();

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

            // Update the metrics
            long duration = System.nanoTime() - startTime;
            metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.DELETE_PRODUCT_FROM_SHELF_BY_VENDOR, duration);
            metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.DELETE_PRODUCT_FROM_SHELF_BY_VENDOR);

        } else {
            metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.DELETE_PRODUCT_FROM_SHELF_BY_VENDOR);
            throw new ProductNotFoundException("Product not found with productId: " + productId + " and shelfNumber: " + shelfNumber);
        }
    }

    // Delete the whole product by the product Id
    @Transactional
    public String deleteProduct(Integer productId) throws ProductNotFoundException {
        // Record the time for metrics
        long startTime = System.nanoTime();
        StringBuilder responseString = new StringBuilder();
        Optional<ProductDetails> productDetails = productDetailsRepository.findByProductId(productId);
        if (productDetails.isPresent()) {
            responseString.append("Product ").append(productDetails.get().getProductName()).append(" was deleted.");
        } else {
            System.out.println("Product was not found here");
            throw new ProductNotFoundException("Product with Id: " + productId + " not found.");
        }

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
            responseString.append(" The following vendors will be removed as they were supplying only this product. Vendor Ids: ")
                    .append(vendorIdsToBeDeleted);
        }

        // Update the metrics
        long duration = System.nanoTime() - startTime;
        metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.DELETE_A_PRODUCT, duration);
        metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.DELETE_A_PRODUCT);

        return responseString.toString();

    }

    @Transactional
    public Product updateProductQuantity(ProductQuantityUpdateDTO product) throws ProductNotFoundException {
        // Record the time for metrics
        long startTime = System.nanoTime();

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

        Product savedProduct = productRepository.save(existingProduct);
        // Update the metrics
        long duration = System.nanoTime() - startTime;
        metricsUtil.recordTimer(MetricsUtil.CustomTimerMetric.UPDATE_PRODUCT_QUANTITY, duration);
        metricsUtil.recordCounter(MetricsUtil.CustomTimerMetric.UPDATE_PRODUCT_QUANTITY);

        return savedProduct;
    }

}
