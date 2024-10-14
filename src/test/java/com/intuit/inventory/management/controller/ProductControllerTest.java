package com.intuit.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.*;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.models.product.ProductListResponseDTO;
import com.intuit.inventory.management.repository.ProductRepository;
import com.intuit.inventory.management.service.ProductService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "page.size=10")
public class ProductControllerTest {

    MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testSaveProduct() throws Exception {
        // Request
        ProductCreateRequestDTO productCreateRequestDTO = new ProductCreateRequestDTO(
                16,
                37,
                23000.00,
                "/vendor1",
                "Phone",
                "Product1",
                "{\"RAM\":\"128GB\",\"color\":\"Black\"}"
        );

        // Expected Response
        ProductDetails productDetails = new ProductDetails(
                1,
                "Product1",
                "{\"RAM\":\"128GB\",\"color\":\"Black\"}",
                "Phone"
        );
        Vendor vendor = new Vendor(1,"/vendor1");
        VendorProductDetails vendorProductDetails = new VendorProductDetails(1,1,23000.00,vendor);
        Product savedProduct = new Product(
                1,
                37,
                16,
                1,
                productDetails,
                vendorProductDetails
        );


        when(productService.registerProduct(any(ProductCreateRequestDTO.class))).thenReturn(savedProduct);

        mockMvc.perform(post("http://localhost:8082/api/product/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productCreateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productDetails.productName").value("Product1"));

        verify(productService, times(1)).registerProduct(any(ProductCreateRequestDTO.class));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        ProductListResponseDTO productListResponseDTO = ProductListResponseDTO.builder()
                .redMin(0)
                .yellowMin(20)
                .greenMin(50)
                .build();

        when(productService.getAllProducts()).thenReturn(productListResponseDTO);

        mockMvc.perform(get("http://localhost:8082/api/product/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redMin").value(0))
                .andExpect(jsonPath("$.yellowMin").value(20))
                .andExpect(jsonPath("$.greenMin").value(50));
    }

    @Test
    public void testGetAllProductsPaged() throws Exception {
        ProductListResponseDTO productListResponseDTO = ProductListResponseDTO.builder()
                .redMin(0)
                .yellowMin(20)
                .greenMin(50)
                .build();

        when(productService.getAllProducts()).thenReturn(productListResponseDTO);

        mockMvc.perform(get("http://localhost:8082/api/product/list/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redMin").value(0))
                .andExpect(jsonPath("$.yellowMin").value(20))
                .andExpect(jsonPath("$.greenMin").value(50));
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
