package com.intuit.inventory.management.strategy.implementation;

import com.intuit.inventory.management.entity.Vendor;
import com.intuit.inventory.management.repository.VendorRepository;
import com.intuit.inventory.management.strategy.FetchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

public class VendorFetchStrategy implements FetchStrategy<Vendor> {
    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public List<Vendor> fetchAll() {
        return vendorRepository.findAll();
    }

    @Override
    public List<Vendor> fetchPaged(Pageable pageable) {
        return vendorRepository.findAll(pageable).stream().collect(Collectors.toList());
    }
}
