package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.product.ProductRequest;
import com.group2.ecommerce.dto.product.ProductResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final int PAGE_SIZE = 10;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final com.group2.ecommerce.service.CategoryService categoryService;

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .imageName(p.getImageName())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .active(p.isActive())
                .build();
    }

    @Override
    public long countAll() {
        return productRepository.count();
    }

    @Override
    public Page<ProductResponse> getProducts(String query, Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        String nameFilter = (query != null && !query.isBlank()) ? query.trim() : null;

        Page<Product> products;
        if (categoryId != null) {
            java.util.List<Long> categoryIds = categoryService.getCategoryAndDescendantIds(categoryId);
            if (categoryIds.isEmpty()) {
                // Category doesn't exist, return empty page
                return Page.empty(pageable);
            }
            products = productRepository.findByNameFilterAndCategoryIds(nameFilter, categoryIds, pageable);
        }else {
            products =productRepository.findByNameFilter(nameFilter, pageable);
        }
        return products.map(this::toResponse);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return toResponse(product);
    }

    @Override
    @Transactional
    public void save(Long id, ProductRequest request) { // Có thể thêm throws IOException
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product;
        if (id == null) {
            product = new Product();
            product.setActive(true);
        } else {
            product = findEntityById(id);
        }

        // Xử lý Upload File
        MultipartFile file = request.getImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                // Định nghĩa đường dẫn lưu file (Trong thư mục static của project)
                String uploadDir = "src/main/resources/static/images/";
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (java.io.InputStream inputStream = file.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    product.setImageName(fileName); // Lưu tên file vào DB
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException("Could not save image file: " + e.getMessage());
            }
        } else if (id != null) {
            // Nếu update mà không chọn file mới, giữ nguyên tên file cũ
            product.setImageName(request.getImageName());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Product product = findEntityById(id);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }

    @Override
    public List<Product> fetchAllProduct() {
        return (List<Product>) productRepository.findAll();

    }

    @Override
    public Product isProductExisted(Long id) {
        return (Product) productRepository.findProductById(id);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        if(!productRepository.existsById(id)) return false;
        Product p = findEntityById(id);
        if(p.isActive()==false) return false;
        toggleStatus(id);
        return true;
    }

    @Override
    @Transactional
    public boolean updateProductQuantityandPrice(Long id, int quantity, BigDecimal price) {
        Product p = isProductExisted(id);
        if(p == null) return false;
        p.setStockQuantity(quantity);
        p.setPrice(price);
        productRepository.save(p);
        return true;    }

    @Override
    public boolean createProduct(Product product) {
        return productRepository.save(product)!=null;
    }

    @Override
    public String saveImage(MultipartFile file) {

        try{
            if(file.isEmpty()){
                throw new RuntimeException("Empty");
            }
            String fileOriginalName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + fileOriginalName;
            String uploadDir = "upload/";
            File uploadFol = new File(uploadDir);
            if(!uploadFol.exists()) uploadFol.mkdir();
            File destination = new File(uploadDir+fileName);
            file.transferTo(destination);
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public long countByStockQuantityLessThan(int quantity) {
        return productRepository.countByStockQuantityLessThan(quantity);
    }

    @Override
    public List<Product> findTop5ByStockQuantityLessThanOrderByStockQuantityAsc(int quantity) {
        return productRepository.findTop5ByStockQuantityLessThanOrderByStockQuantityAsc(quantity);
    }

    // Trong ProductServiceImpl.java
    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }
}
