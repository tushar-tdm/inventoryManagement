package com.intuit.inventory.management.factory;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.exceptions.AddingNewVendorWithoutVendorLinkException;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.VendorRepository;

import java.util.Optional;

public class VendorFactory {
    public static Vendor createOrFetchVendor(ProductCreateRequestDTO productRequest, VendorRepository vendorRepository)
            throws AddingNewVendorWithoutVendorLinkException {

        // Check if there's a vendor by the given link
        Optional<Vendor> existingVendor = vendorRepository.findByVendorLink(productRequest.getVendorLink());

        if (existingVendor.isPresent()) {
            return existingVendor.get();
        } else {
            if (productRequest.getVendorLink() == null) {
                throw new AddingNewVendorWithoutVendorLinkException("Vendor link should be provided while adding a new Vendor");
            } else {
                Vendor newVendor = new Vendor();
                newVendor.setVendorLink(productRequest.getVendorLink());
                return vendorRepository.save(newVendor);
            }
        }
    }
}
