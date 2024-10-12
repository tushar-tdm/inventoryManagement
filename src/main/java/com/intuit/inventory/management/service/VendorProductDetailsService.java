package com.intuit.inventory.management.service;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.entity.VendorProductDetails;
import com.intuit.inventory.management.entity.VendorProductKey;
import com.intuit.inventory.management.exceptions.VendorNotFoundException;
import com.intuit.inventory.management.exceptions.VendorProductNotFoundException;
import com.intuit.inventory.management.models.Vendor.VendorProductUpdateDTO;
import com.intuit.inventory.management.repository.VendorProductDetailsRepository;
import com.intuit.inventory.management.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorProductDetailsService {
    @Autowired
    VendorProductDetailsRepository vendorProductDetailsRepository;

    @Autowired
    VendorRepository vendorRepository;


    public List<VendorProductDetails> getProductsSoldByVendor(Integer vendorId) throws VendorNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);
        if (vendor.isPresent()) {
            return vendorProductDetailsRepository.findAllByVendorId(vendorId);
        } else {
            throw new VendorNotFoundException("Vendor with id: " + vendorId + " not found");
        }
    }

    public VendorProductDetails updateVendorProductDetails(VendorProductUpdateDTO vendorProduct) throws VendorNotFoundException, VendorProductNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findById(vendorProduct.getVendorId());
        if (vendor.isEmpty()){
            throw new VendorNotFoundException("Vendor with id: " + vendorProduct.getVendorId() + " not found");
        }

        VendorProductKey vendorProductKey = new VendorProductKey(vendorProduct.getVendorId(), vendorProduct.getProductId());
        if (vendorProductDetailsRepository.findById(vendorProductKey).isEmpty()) {
            throw new VendorProductNotFoundException(
                    "The vendor " + vendorProduct.getVendorId() + " does not sell " + vendorProduct.getProductId()
            );
        }
        VendorProductDetails vendorProductDetails = new VendorProductDetails();
        vendorProductDetails.setVendor(vendor.get());
        vendorProductDetails.setProductId(vendorProduct.getProductId());
        vendorProductDetails.setVendorId(vendorProduct.getVendorId());
        vendorProductDetails.setPrice(vendorProduct.getPrice());

        return vendorProductDetailsRepository.save(vendorProductDetails);
    }
}
