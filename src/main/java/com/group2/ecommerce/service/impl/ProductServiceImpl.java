package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.repository.ProductRepository;
import com.group2.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public long countAll() {
        return productRepository.count();
    }
}
