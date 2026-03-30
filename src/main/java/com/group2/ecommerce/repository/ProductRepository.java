package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByStockQuantityLessThan(int quantity);
    List<Product> findTop5ByStockQuantityLessThanOrderByStockQuantityAsc(int quantity);
    Product findProductById(Long id);
    // Fetch all active products with pagination and sorting
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    // Fetch active products by specific category with pagination
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    // Filter by category tree (category or any subcategory) + Keyword + Pagination
   @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Product> findByCategoryTreeAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
    
   @Query("SELECT p FROM Product p WHERE " +
           "(:nameFilter IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:nameFilter AS string), '%')))")
    Page<Product> findByNameFilter(@Param("nameFilter") String nameFilter, Pageable pageable);

    // 3. Cập nhật hàm này (thêm CAST(:nameFilter AS string))
    @Query("SELECT p FROM Product p WHERE " +
           "(:nameFilter IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:nameFilter AS string), '%'))) " +
           "AND p.category.id IN :categoryIds")
    Page<Product> findByNameFilterAndCategoryIds(@Param("nameFilter") String nameFilter,
                                                 @Param("categoryIds") java.util.List<Long> categoryIds,
                                                 Pageable pageable);
}





