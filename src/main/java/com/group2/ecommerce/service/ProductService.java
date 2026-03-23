package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.product.ProductRequest;
import com.group2.ecommerce.dto.product.ProductResponse;
import com.group2.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    long countAll();

    Page<ProductResponse> getProducts(String query, Long categoryId, int page);

    ProductResponse findById(Long id);

    void save(Long id, ProductRequest request) throws java.io.IOException;

    void toggleStatus(Long id);

    List<Product> fetchAllProduct();

    Product isProductExisted(Long id);
    boolean deleteProduct(Long id);

    boolean updateProductQuantity(Long id, int quantity);

    boolean createProduct(Product product);

    String saveImage(MultipartFile file);
}
