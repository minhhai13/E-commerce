package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.product.ProductRequest;
import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.repository.ProductRepository;
import com.group2.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final int PAGE_SIZE = 10;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public long countAll() {
        return productRepository.count();
    }

    @Override
    public Page<Product> getProducts(String query, Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        String nameFilter = (query != null && !query.isBlank()) ? query.trim() : null;
        return productRepository.findByFilter(nameFilter, categoryId, pageable);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Override
    @Transactional
    public void save(Long id, ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));

        if (id == null) {
            // Create
            Product product = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stockQuantity(request.getStockQuantity())
                    .imageName(request.getImageName())
                    .category(category)
                    .isActive(true)
                    .build();
            productRepository.save(product);
        } else {
            // Update
            Product product = findById(id);
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setImageName(request.getImageName());
            product.setCategory(category);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Product product = findById(id);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }
}
