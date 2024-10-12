package com.intuit.inventory.management.repository;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.entity.VendorProductDetails;
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
@RunWith(SpringRunner.class)
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

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        productDetailRepository.deleteAll();
        vendorProductDetailsRepository.deleteAll();
        vendorRepository.deleteAll();
        // Create sample Product objects and save them to the database
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setShelfNumber(101);
        product1.setVendorId(10);
        product1.setQuantity(50);
        ProductDetails productDetails1 = new ProductDetails();
        productDetails1.setProductDescription("{\"size\":\"M\"}");
        productDetails1.setProductName("shirt");
        productDetails1.setCategory("Clothes");
        productDetails1.setProductId(1);
        productDetailRepository.save(productDetails1);
        VendorProductDetails vendorProductDetails1 = new VendorProductDetails();
        vendorProductDetails1.setVendorId(10);
        vendorProductDetails1.setVendor(vendorRepository.save(new Vendor(10, "/vendor/link/10")));
        vendorProductDetails1.setProductId(1);
        vendorProductDetails1.setPrice(350.00);
        vendorProductDetailsRepository.save(vendorProductDetails1);
        product1.setProductDetails(productDetails1);
        product1.setVendorProductDetails(vendorProductDetails1);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setShelfNumber(102);
        product2.setVendorId(20);
        product2.setQuantity(100);
        ProductDetails productDetails2 = new ProductDetails();
        productDetails2.setProductDescription("{\"size\":\"L\"}");
        productDetails2.setProductName("shirt");
        productDetails2.setCategory("Clothes");
        productDetails2.setProductId(2);
        productDetailRepository.save(productDetails2);
        VendorProductDetails vendorProductDetails2 = new VendorProductDetails();
        vendorProductDetails2.setVendorId(20);
        vendorProductDetails2.setVendor(vendorRepository.save(new Vendor(20, "/vendor/link/20")));
        vendorProductDetails2.setProductId(2);
        vendorProductDetails2.setPrice(350.00);
        vendorProductDetailsRepository.save(vendorProductDetails2);
        product1.setProductDetails(productDetails2);
        product1.setVendorProductDetails(vendorProductDetails2);
        productRepository.save(product2);
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
    public void testFindByProductId() {
        Optional<Product> product = productRepository.findByProductId(1);
        assertTrue(product.isPresent());
        assertEquals(1, product.get().getProductId());
    }

    // Test for deleteByProductId()
    @Test
    public void testDeleteByProductId() {
        productRepository.deleteByProductId(1);
        Optional<Product> product = productRepository.findByProductId(1);
        assertTrue(product.isEmpty());
    }

    // Test for findShelfNumbersByProductIdAndVendorId()
    @Test
    public void testFindShelfNumbersByProductIdAndVendorId() {
        List<Integer> shelfNumbers = productRepository.findShelfNumbersByProductIdAndVendorId(10, 1);
        assertNotNull(shelfNumbers);
        assertEquals(1, shelfNumbers.size());
        assertEquals(101, shelfNumbers.get(0));
    }

    // Test for deleteByVendorId()
    @Test
    public void testDeleteByVendorId() {
        productRepository.deleteByVendorId(10);
        Optional<Product> product = productRepository.findByProductId(1);
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
