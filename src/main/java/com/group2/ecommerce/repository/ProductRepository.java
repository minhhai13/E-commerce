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

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductById(Long id);
    // Fetch all active products with pagination and sorting
    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    // Fetch active products by specific category with pagination
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    // Filter by category tree (category or any subcategory) + Keyword + Pagination
    @Query("SELECT p FROM Product p LEFT JOIN p.category c " +
            "WHERE p.isActive = true " +
            "AND (:categoryId IS NULL OR c.id = :categoryId OR c.parent.id = :categoryId) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByCategoryTreeAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByNameFilter(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(p.category.id IN :categoryIds)")
    Page<Product> findByNameFilterAndCategoryIds(@Param("name") String name,
                                                 @Param("categoryIds") java.util.List<Long> categoryIds,
                                                 Pageable pageable);
}





