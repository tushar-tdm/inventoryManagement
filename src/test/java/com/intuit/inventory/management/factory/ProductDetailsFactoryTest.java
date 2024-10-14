package com.intuit.inventory.management.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.exceptions.AddingProductWithoutProductNameOrCategory;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ProductDetailsFactoryTest {

    @Mock
    private ProductDetailRepository productDetailRepository;

    @InjectMocks
    private ProductDetailsFactory productDetailsFactory;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateOrFetchProductDetails_NewProduct_Success() throws Exception {
        ProductCreateRequestDTO productRequest = new ProductCreateRequestDTO();
        productRequest.setProductName("Shirt");
        productRequest.setCategory("Clothes");
        String productDescription = "{\"size\":\"M\"}";
        productRequest.setProductDescription(objectMapper.readValue(productDescription, Object.class));

        ProductDetails productDetails = ProductDetails.builder()
                .productId(1).
                productName("Shirt").
                category("Clothes").
                productDescription(productDescription).
                build();


        Mockito.when(productDetailRepository.findByProductName(productRequest.getProductName())).thenReturn(Optional.empty());
        Mockito.when(productDetailRepository.save(any(ProductDetails.class))).thenReturn(productDetails);

        ProductDetails result = ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailRepository);

        // Verify
        Mockito.verify(productDetailRepository).save(any(ProductDetails.class));
        Assert.assertNotNull(result);
        Assert.assertEquals("Shirt", result.getProductName());
        Assert.assertEquals("Clothes", result.getCategory());
        Assert.assertEquals("{\"size\":\"M\"}", result.getProductDescription());
    }

    @Test
    public void testCreateOrFetchProductDetails_ExistingProduct_Success() throws Exception {
        ProductCreateRequestDTO productRequest = new ProductCreateRequestDTO();
        productRequest.setProductName("Shirt");

        ProductDetails existingProductDetails = new ProductDetails();
        existingProductDetails.setProductName("Shirt");
        existingProductDetails.setCategory("Clothes");

        Mockito.when(productDetailRepository.findByProductName(productRequest.getProductName()))
                .thenReturn(Optional.of(existingProductDetails));


        ProductDetails result = ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailRepository);

        // Verify
        Assert.assertNotNull(result);
        Assert.assertEquals("Shirt", result.getProductName());
        Assert.assertEquals("Clothes", result.getCategory());
        Mockito.verify(productDetailRepository, Mockito.never()).save(any(ProductDetails.class));
    }

    @Test()
    public void testCreateOrFetchProductDetails_NewProductWithoutNameOrCategory_ThrowsException() throws Exception {
        ProductCreateRequestDTO productRequest = new ProductCreateRequestDTO();
        productRequest.setProductName(null);
        productRequest.setCategory(null);

        Mockito.when(productDetailRepository.findByProductName(Mockito.anyString())).thenReturn(Optional.empty());

        Assert.assertThrows(AddingProductWithoutProductNameOrCategory.class, () -> {
            ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailRepository);
        });

    }

    @Test
    public void testCreateOrFetchProductDetails_ProductDescriptionJsonProcessingError_ThrowsException() throws Exception {
        // Arrange
        ProductCreateRequestDTO productRequest = new ProductCreateRequestDTO();
        productRequest.setProductName("Shirt");
        productRequest.setCategory("Clothes");
        productRequest.setProductDescription(new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Invalid description");
            }
        });

        Mockito.when(productDetailRepository.findByProductName(productRequest.getProductName())).thenReturn(Optional.empty());

        Assert.assertThrows(RuntimeException.class, () -> {
            ProductDetailsFactory.createOrFetchProductDetails(productRequest, productDetailRepository);
        });
    }
}

