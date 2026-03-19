package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

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

