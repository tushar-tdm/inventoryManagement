package com.intuit.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.*;
import com.intuit.inventory.management.exceptions.ProductNotFoundException;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.models.product.ProductListPagedResponseDTO;
import com.intuit.inventory.management.models.product.ProductListResponseDTO;
import com.intuit.inventory.management.models.product.ProductQuantityUpdateDTO;
import com.intuit.inventory.management.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

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
        ProductListPagedResponseDTO productListResponseDTO = ProductListPagedResponseDTO.builder()
                .redMin(0)
                .yellowMin(20)
                .greenMin(50)
                .pageSize(10)
                .build();

        when(productService.getAllProductsPaged(any(Pageable.class))).thenReturn(productListResponseDTO);

        mockMvc.perform(get("http://localhost:8082/api/product/list/page/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redMin").value(0))
                .andExpect(jsonPath("$.yellowMin").value(20))
                .andExpect(jsonPath("$.greenMin").value(50));
    }

    @Test
    public void testDeleteProductByVendorInShelf() throws Exception {
        mockMvc.perform(delete("http://localhost:8082/api/product/delete/1/shelf/3/vendor/7"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProductByProductId() throws Exception {
        mockMvc.perform(delete("http://localhost:8082/api/product/deleteProduct/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProductByProductName() throws Exception {
        mockMvc.perform(delete("http://localhost:8082/api/product/delete/productName/apple"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProductByVendorInShelfFailure() throws Exception {
        // Arrange: Mock the service to throw an exception when called
        doThrow(new ProductNotFoundException()).when(productService).deleteProductByIdShelfNumberAndVendorId(1, 3, 7);

        // Act and Assert: Perform the request and expect a 404 status with the custom error message
        mockMvc.perform(delete("/api/product/delete/1/shelf/3/vendor/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value("NOT_FOUND"));
    }

    @Test
    public void testDeleteProductByProductIdFailure() throws Exception {
        doThrow(new ProductNotFoundException()).when(productService).deleteProductById(anyInt());

        mockMvc.perform(delete("http://localhost:8082/api/product/deleteProduct/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value("NOT_FOUND"));
    }

    @Test
    public void testDeleteProductByProductNameFailure() throws Exception {
        doThrow(new ProductNotFoundException()).when(productService).deleteProductByName(anyString());

        mockMvc.perform(delete("http://localhost:8082/api/product/delete/productName/apple"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value("NOT_FOUND"));
    }

    @Test
    public void testUpdateProductQuantity() throws Exception {
        ProductDetails productDetails = new ProductDetails(1, "Iphone 15 Pro", "Phone", "{\"size\":\"256GB\",\"RAM\":\"8GB\"}");
        Vendor vendor = new Vendor(7,"/vendor/7");
        VendorProductDetails vendorProductDetails = new VendorProductDetails(7,1,89999.10,vendor);
        Product updatedProduct = Product.builder()
                .productId(1)
                .shelfNumber(3)
                .vendorId(7)
                .quantity(10)
                .productDetails(productDetails)
                .vendorProductDetails(vendorProductDetails)
                .build();

        ProductQuantityUpdateDTO productQuantityUpdateDTO = new ProductQuantityUpdateDTO(1, 3, 7, 35);
        when(productService.updateProductQuantity(any(productQuantityUpdateDTO.getClass()))).thenReturn(updatedProduct);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(productQuantityUpdateDTO);

        mockMvc.perform(put("http://localhost:8082/api/product/updateQuantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());
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
