package com.intuit.inventory.management.service;

import com.intuit.inventory.management.models.product.ProductInformationDTO;
import com.intuit.inventory.management.models.product.ProductListPagedResponseDTO;
import com.intuit.inventory.management.models.product.ProductListResponseDTO;
import com.intuit.inventory.management.repository.ProductRepository;
import com.intuit.inventory.management.strategy.implementation.ProductFetchStrategy;
import com.intuit.inventory.management.utils.MetricsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    private ProductFetchStrategy fetchStrategy;

    @Mock
    private MetricsUtil metricsUtil;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAllProductsPaged() {
        ProductInformationDTO productInformation1 = new ProductInformationDTO(
                "Product 1",
                "Category 1",
                "Description 1",
                10,
                100.00,
                "/vendor1",
                1,
                1,
                1
        );

        ProductInformationDTO productInformation2 = new ProductInformationDTO(
                "Product 2",
                "Category 2",
                "Description 2",
                5,
                200.00,
                "/vendor2",
                2,
                2,
                2
        );

        List<ProductInformationDTO> products = Arrays.asList(productInformation1, productInformation2);

        when(fetchStrategy.fetchPaged(any(Pageable.class))).thenReturn(products);

        ProductListPagedResponseDTO response = productService.getAllProductsPaged(Pageable.unpaged());

        // Assertions
        assertNotNull(response);
        assertEquals(2, response.getProducts().size());

        ProductInformationDTO productInfo1 = response.getProducts().get(0);
        assertEquals("Product 1", productInfo1.getProductName());
        assertEquals("Category 1", productInfo1.getCategory());
        assertEquals("Description 1", productInfo1.getProductDescription());
        assertEquals(10, productInfo1.getQuantity());
        assertEquals(100, productInfo1.getPrice());
        assertEquals("/vendor1", productInfo1.getVendorLink());
        assertEquals(1, productInfo1.getShelfNumber());
        assertEquals(1, productInfo1.getProductId());
        assertEquals(1, productInfo1.getVendorId());

        ProductInformationDTO productInfo2 = response.getProducts().get(1);
        assertEquals("Product 2", productInfo2.getProductName());
        assertEquals("Category 2", productInfo2.getCategory());
        assertEquals("Description 2", productInfo2.getProductDescription());
        assertEquals(5, productInfo2.getQuantity());
        assertEquals(200, productInfo2.getPrice());
        assertEquals("/vendor2", productInfo2.getVendorLink());
        assertEquals(2, productInfo2.getShelfNumber());
        assertEquals(2, productInfo2.getProductId());
        assertEquals(2, productInfo2.getVendorId());
    }

    @Test
    public void testGetAllProducts() {
        ProductInformationDTO productInformation1 = new ProductInformationDTO(
                "Product 1",
                "Category 1",
                "Description 1",
                10,
                100.00,
                "/vendor1",
                1,
                1,
                1
        );

        ProductInformationDTO productInformation2 = new ProductInformationDTO(
                "Product 2",
                "Category 2",
                "Description 2",
                5,
                200.00,
                "/vendor2",
                2,
                2,
                2
        );

        List<ProductInformationDTO> products = Arrays.asList(productInformation1, productInformation2);

        when(fetchStrategy.fetchAll()).thenReturn(products);

        ProductListResponseDTO response = productService.getAllProducts();

        // Assertions
        assertNotNull(response);
        assertEquals(2, response.getProducts().size());

        ProductInformationDTO productInfo1 = response.getProducts().get(0);
        assertEquals("Product 1", productInfo1.getProductName());
        assertEquals("Category 1", productInfo1.getCategory());
        assertEquals("Description 1", productInfo1.getProductDescription());
        assertEquals(10, productInfo1.getQuantity());
        assertEquals(100, productInfo1.getPrice());
        assertEquals("/vendor1", productInfo1.getVendorLink());
        assertEquals(1, productInfo1.getShelfNumber());
        assertEquals(1, productInfo1.getProductId());
        assertEquals(1, productInfo1.getVendorId());

        ProductInformationDTO productInfo2 = response.getProducts().get(1);
        assertEquals("Product 2", productInfo2.getProductName());
        assertEquals("Category 2", productInfo2.getCategory());
        assertEquals("Description 2", productInfo2.getProductDescription());
        assertEquals(5, productInfo2.getQuantity());
        assertEquals(200, productInfo2.getPrice());
        assertEquals("/vendor2", productInfo2.getVendorLink());
        assertEquals(2, productInfo2.getShelfNumber());
        assertEquals(2, productInfo2.getProductId());
        assertEquals(2, productInfo2.getVendorId());
    }
}
