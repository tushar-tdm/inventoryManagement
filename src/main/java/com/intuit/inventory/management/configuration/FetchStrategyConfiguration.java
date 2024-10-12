package com.intuit.inventory.management.configuration;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.models.product.ProductInformationDTO;
import com.intuit.inventory.management.strategy.FetchStrategy;
import com.intuit.inventory.management.strategy.implementation.ProductFetchStrategy;
import com.intuit.inventory.management.strategy.implementation.VendorFetchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FetchStrategyConfiguration {

    @Bean
    public FetchStrategy<Vendor> vendorFetchStrategy() {
        return new VendorFetchStrategy();
    }

    @Bean
    public FetchStrategy<ProductInformationDTO> productFetchStrategy() {
        return new ProductFetchStrategy();
    }

    @Bean
    public ProductFetchStrategy concreteProductFetchStrategy() {
        return new ProductFetchStrategy(); // this creates a ProductFetchStrategy bean
    }

    @Bean
    public VendorFetchStrategy concreteVendorFetchStrategy() {
        return new VendorFetchStrategy();
    }
}