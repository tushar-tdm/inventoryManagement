package com.intuit.inventory.management.strategy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FetchStrategy<T> {
    List<T> fetchAll();
    List<T> fetchPaged(Pageable pageable);
}
