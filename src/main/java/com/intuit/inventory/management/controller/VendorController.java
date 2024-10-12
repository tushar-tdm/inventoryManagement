package com.intuit.inventory.management.controller;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.exceptions.VendorNotFoundException;
import com.intuit.inventory.management.models.Vendor.VendorDTO;
import com.intuit.inventory.management.service.VendorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    private final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @GetMapping("/list")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        logger.info("Fetching all of the vendors");
        List<Vendor> vendors = vendorService.getAllVendor();
        return ResponseEntity.ok(vendors);
    }

    /** ****************************************************
     * GET CALLS
     * ****************************************************
     */
    @GetMapping("/{vendorId}")
    public ResponseEntity<Vendor> getVendorByVendorId(@PathVariable Integer vendorId) throws VendorNotFoundException {
        logger.info("Getting the vendor by vendorId");
        Vendor vendor = vendorService.getVendorByVendorId(vendorId);
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/vendorByLink")
    public ResponseEntity<Integer> getVendorIdByVendorLink(@RequestParam("vendorLink") String vendorLink) {
        logger.info("Getting the vendorId by vendor link");
        Integer vendorId = vendorService.getVendorIdByVendorLink(vendorLink);
        return ResponseEntity.ok(vendorId);
    }

    /** ****************************************************
     * PUT CALLS
     * ****************************************************
     */

    @PutMapping("/update")
    public ResponseEntity<Vendor> updateVendor(@Valid @RequestBody VendorDTO vendor) throws VendorNotFoundException {
        logger.info("Updating vendor by id: " + vendor.getVendorId());
        Vendor updatedVendor = vendorService.updateVendor(vendor);
        return ResponseEntity.ok(updatedVendor);
    }

    /** ****************************************************
     * DELETE CALLS
     * ****************************************************
     */

    @DeleteMapping("/delete/{vendorId}")
    public ResponseEntity<String> deleteVendor(@PathVariable Integer vendorId) throws VendorNotFoundException {
        logger.info("Deleting the vendor of id: " + vendorId);
        vendorService.deleteVendor(vendorId);
        return ResponseEntity.ok("Vendor with id:" + vendorId + " deleted successfully");
    }
}
