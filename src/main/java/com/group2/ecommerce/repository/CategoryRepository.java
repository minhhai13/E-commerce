package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Fetch only active root categories (those without a parent)
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parent IS NULL")
    List<Category> findActiveRootCategories();

    // Fetch all active categories
    List<Category> findAllByIsActiveTrue();
}
