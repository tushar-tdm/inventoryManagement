package com.intuit.inventory.management.service;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.exceptions.VendorNotFoundException;
import com.intuit.inventory.management.models.Vendor.VendorDTO;
import com.intuit.inventory.management.repository.ProductDetailRepository;
import com.intuit.inventory.management.repository.ProductRepository;
import com.intuit.inventory.management.repository.VendorProductDetailsRepository;
import com.intuit.inventory.management.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class VendorService {

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    VendorProductDetailsRepository vendorProductDetailsRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;

    public List<Vendor> getAllVendor() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorByVendorId(Integer vendorId) throws VendorNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);
        if (vendor.isPresent()){
            return vendor.get();
        } else {
            throw new VendorNotFoundException("Vendor with id: " + vendorId + " not found");
        }
    }

    public Vendor updateVendor(VendorDTO vendor) throws VendorNotFoundException {
        if (vendorRepository.findById(vendor.getVendorId()).isEmpty()) {
            throw new VendorNotFoundException("Vendor with id: " + vendor.getVendorId() + " not found");
        }

        Vendor updatedVendor = new Vendor();
        updatedVendor.setVendorId(vendor.getVendorId());
        updatedVendor.setVendorLink(vendor.getVendorLink());

        return vendorRepository.save(updatedVendor);
    }

    public ResponseEntity<String> deleteVendor(Integer vendorId) throws VendorNotFoundException {
        if (vendorRepository.findById(vendorId).isEmpty()) {
            throw new VendorNotFoundException("Vendor with id: " + vendorId + " not found");
        }
        vendorRepository.deleteById(vendorId);
        List<Integer> productIds = vendorProductDetailsRepository.getProductIdByVendorId(vendorId);
        vendorProductDetailsRepository.deleteByVendorId(vendorId);

        productRepository.deleteByVendorId(vendorId);

        for (int prod_id : productIds) {
            // check if this product still exist in other shelves by other vendors
            if (productRepository.findByProductId(prod_id).isEmpty()) {
                productDetailRepository.deleteById(prod_id);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("Successfully delete vendor " + vendorId + " and their products");
    }

    public Integer getVendorIdByVendorLink(String vendorLink) {
        System.out.println("Vendor Link is: " + vendorLink);
        return vendorRepository.findVendorIdByVendorLink(vendorLink).getVendorId();
    }
}
