package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.entity.VendorProductDetails;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorProductDetailsRepository vendorProductDetailsRepository;

    private Integer productId1;
    private Integer vendorId1;
    private Integer productId2;
    private Integer vendorId2;


    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        productDetailRepository.deleteAll();
        vendorProductDetailsRepository.deleteAll();
        vendorRepository.deleteAll();

        // Create sample Product objects and save them to the database
        // ------ PRODUCT 1 ---------
        Vendor newVendor = new Vendor();
        newVendor.setVendorLink("/vendor/link/10");
        Vendor savedVendor1 = vendorRepository.save(newVendor);

        ProductDetails productDetails1 = new ProductDetails();
        productDetails1.setProductDescription("{\"size\":\"M\"}");
        productDetails1.setProductName("shirt");
        productDetails1.setCategory("Clothes");
        ProductDetails savedProductDetails1 = productDetailRepository.save(productDetails1);
        System.out.println("productDetails1 product id: " + savedProductDetails1.getProductId());

        Product product1 = new Product();
        product1.setProductId(savedProductDetails1.getProductId());
        product1.setShelfNumber(101);
        product1.setVendorId(savedVendor1.getVendorId());
        product1.setQuantity(50);

        VendorProductDetails vendorProductDetails1 = new VendorProductDetails();
        vendorProductDetails1.setVendorId(savedVendor1.getVendorId());
        vendorProductDetails1.setVendor(savedVendor1);
        vendorProductDetails1.setProductId(savedProductDetails1.getProductId());
        vendorProductDetails1.setPrice(350.00);
        vendorProductDetailsRepository.save(vendorProductDetails1);

        product1.setProductDetails(savedProductDetails1);
        product1.setVendorProductDetails(vendorProductDetails1);
        productRepository.save(product1);

        productId1 = savedProductDetails1.getProductId();
        vendorId1 = savedVendor1.getVendorId();

        // ------ PRODUCT 2 ---------
        Vendor newVendor2 = new Vendor();
        newVendor2.setVendorLink("/vendor/link/20");
        Vendor savedVendor2 = vendorRepository.save(newVendor2);

        ProductDetails productDetails2 = new ProductDetails();
        productDetails2.setProductDescription("{\"size\":\"L\"}");
        productDetails2.setProductName("shirt");
        productDetails2.setCategory("Clothes");
        ProductDetails savedProductDetails2 = productDetailRepository.save(productDetails2);
        System.out.println("productDetails2 product id: " + savedProductDetails2.getProductId());

        Product product2 = new Product();
        product2.setProductId(savedProductDetails2.getProductId());
        product2.setShelfNumber(102);
        product2.setVendorId(savedVendor2.getVendorId());
        product2.setQuantity(100);

        VendorProductDetails vendorProductDetails2 = new VendorProductDetails();
        vendorProductDetails2.setVendorId(savedVendor2.getVendorId());
        newVendor.setVendorLink("/vendor/link/20");
        vendorProductDetails2.setVendor(savedVendor2);
        vendorProductDetails2.setProductId(savedProductDetails2.getProductId());
        vendorProductDetails2.setPrice(350.00);
        vendorProductDetailsRepository.save(vendorProductDetails2);
        product2.setProductDetails(savedProductDetails2);
        product2.setVendorProductDetails(vendorProductDetails2);
        productRepository.save(product2);

        productId2 = savedProductDetails2.getProductId();
        vendorId2 = savedVendor2.getVendorId();
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
        productDetailRepository.deleteAll();
        vendorProductDetailsRepository.deleteAll();
        vendorRepository.deleteAll();
    }

    // Test for findByProductId()
    @Test
    public void testNumberOfRowsInTables() {
        List<Product> products = productRepository.findAll();
        List<ProductDetails> productDetails = productDetailRepository.findAll();
        List<VendorProductDetails> vendorProductDetails = vendorProductDetailsRepository.findAll();
        List<Vendor> vendors = vendorRepository.findAll();

        assertEquals(2, products.size());
        assertEquals(2, productDetails.size());
        assertEquals(2, vendorProductDetails.size());
        assertEquals(2, vendors.size());
    }

    @Test
    public void testFindByProductId() {
        Optional<Product> product = productRepository.findByProductId(productId1);
        assertTrue(product.isPresent());
        assertEquals(productId1, product.get().getProductId());
    }

    // Test for deleteByProductId()
    @Test
    public void testDeleteByProductId() {
        productRepository.deleteByProductId(productId1);
        Optional<Product> product = productRepository.findByProductId(productId1);
        assertTrue(product.isEmpty());
    }

    // Test for findShelfNumbersByProductIdAndVendorId()
    @Test
    public void testFindShelfNumbersByProductIdAndVendorId() {
        List<Integer> shelfNumbers = productRepository.findShelfNumbersByProductIdAndVendorId(vendorId1, productId1);
        assertNotNull(shelfNumbers);
        assertEquals(1, shelfNumbers.size());
        assertEquals(101, shelfNumbers.get(0));
    }

    // Test for deleteByVendorId()
    @Test
    public void testDeleteByVendorId() {
        productRepository.deleteByVendorId(vendorId1);
        Optional<Product> product = productRepository.findByProductId(productId1);
        assertTrue(product.isEmpty());
    }

    // Test for findAll(Pageable pageable)
    @Test
    public void testFindAllWithPaging() {
        Pageable pageable = PageRequest.of(0, 1); // page 0, size 1
        Page<Product> productPage = productRepository.findAll(pageable);

        assertNotNull(productPage);
        assertEquals(1, productPage.getContent().size());
    }
}
