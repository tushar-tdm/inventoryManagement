package com.intuit.inventory.management.strategy.implementation;

import com.intuit.inventory.management.entity.Product;
import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.entity.VendorProductDetails;
import com.intuit.inventory.management.models.product.ProductInformationDTO;
import com.intuit.inventory.management.repository.ProductRepository;
import com.intuit.inventory.management.strategy.FetchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProductFetchStrategy implements FetchStrategy<ProductInformationDTO> {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductInformationDTO> convertProductToProductInformation(List<Product> products) {
        return products.stream().map(product -> {
            ProductDetails details = product.getProductDetails();
            VendorProductDetails vendorProductDetails = product.getVendorProductDetails();
            Vendor vendor = vendorProductDetails.getVendor();

            return new ProductInformationDTO(
                    details.getProductName(),
                    details.getCategory(),
                    details.getProductDescription(),
                    product.getQuantity(),
                    vendorProductDetails.getPrice(),
                    vendor.getVendorLink(),
                    product.getShelfNumber(),
                    product.getProductId(),
                    vendorProductDetails.getVendorId()
            );
        }).toList();
    }

    @Override
    public List<ProductInformationDTO> fetchAll() {
        return convertProductToProductInformation(productRepository.findAll());
    }

    @Override
    public List<ProductInformationDTO> fetchPaged(Pageable pageable) {
        return convertProductToProductInformation(productRepository.findAll(pageable).getContent());
    }
}
