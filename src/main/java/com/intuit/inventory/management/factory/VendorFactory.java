package com.intuit.inventory.management.factory;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.exceptions.AddingNewVendorWithoutVendorLinkException;
import com.intuit.inventory.management.models.product.ProductCreateRequestDTO;
import com.intuit.inventory.management.repository.VendorRepository;

import java.util.Optional;

public class VendorFactory {
    public static Vendor createOrFetchVendor(ProductCreateRequestDTO productRequest, VendorRepository vendorRepository)
            throws AddingNewVendorWithoutVendorLinkException {
        Optional<Vendor> vendorOptional = vendorRepository.findById(productRequest.getVendorId());
        if (vendorOptional.isPresent()) {
            return vendorOptional.get();
        } else {
            if (productRequest.getVendorLink() == null) {
                throw new AddingNewVendorWithoutVendorLinkException("Vendor link should be provided only while adding a new Vendor");
            } else {
                Vendor newVendor = new Vendor();
                newVendor.setVendorId(productRequest.getVendorId());
                newVendor.setVendorLink(productRequest.getVendorLink());
                return vendorRepository.save(newVendor);
            }
        }
    }
}
