package com.intuit.inventory.management.controller;

import com.intuit.inventory.management.entity.VendorProductDetails;
import com.intuit.inventory.management.exceptions.VendorNotFoundException;
import com.intuit.inventory.management.exceptions.VendorProductNotFoundException;
import com.intuit.inventory.management.models.Vendor.VendorProductUpdateDTO;
import com.intuit.inventory.management.service.VendorProductDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/vendorProduct")
@RestController
public class VendorProductController {
    @Autowired
    private VendorProductDetailsService vendorProductDetailsService;

    /** ****************************************************
     * GET CALLS
     * ****************************************************
     */
    @GetMapping("/vendorId/{vendorId}/products")
    public ResponseEntity<List<VendorProductDetails>> getProductsSoldByVendor(@PathVariable Integer vendorId) throws VendorNotFoundException {
        List<VendorProductDetails> vendorProductDetails = vendorProductDetailsService.getProductsSoldByVendor(vendorId);
        return ResponseEntity.ok(vendorProductDetails);
    }

    /** ****************************************************
     * PUT CALLS
     * ****************************************************
     */

    @PutMapping(value = "/update")
    public ResponseEntity<VendorProductDetails> updateVendorProductDetails(@Valid @RequestBody VendorProductUpdateDTO vendorProduct)
            throws VendorProductNotFoundException, VendorNotFoundException
    {
        VendorProductDetails vendorProductDetails = vendorProductDetailsService.updateVendorProductDetails(vendorProduct);
        return ResponseEntity.ok(vendorProductDetails);
    }
}
