package com.intuit.inventory.management.factory;

import com.intuit.inventory.management.entity.ProductDetails;
import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.entity.VendorProductDetails;
import com.intuit.inventory.management.entity.VendorProductKey;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.VendorProductDetailsRepository;

public class VendorProductDetailsFactory {

    public static VendorProductDetails createOrFetchVendorProductDetails(
            VendorProductKey vendorProductKey,
            VendorProductDetailsRepository vendorProductDetailsRepository,
            ProductCreateRequestDTO productRequest,
            int productId,
            int vendorId,
            Vendor vendor
    ) {
        return vendorProductDetailsRepository.findById(vendorProductKey)
            .orElseGet(() -> {
                // check if there is a product by the product name
                VendorProductDetails newVendorProductDetails = new VendorProductDetails();
                newVendorProductDetails.setVendorId(vendorId);
                newVendorProductDetails.setProductId(productId);
                newVendorProductDetails.setPrice(productRequest.getPrice());

                newVendorProductDetails.setVendor(vendor);
                return vendorProductDetailsRepository.save(newVendorProductDetails);
            });
    }
}
