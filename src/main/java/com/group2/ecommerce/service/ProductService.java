package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.product.ProductRequest;
import com.group2.ecommerce.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    long countAll();

    Page<Product> getProducts(String query, Long categoryId, int page);

    Product findById(Long id);

    void save(Long id, ProductRequest request);

    void toggleStatus(Long id);
}
