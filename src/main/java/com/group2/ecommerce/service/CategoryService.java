package com.group2.ecommerce.service;

import com.group2.ecommerce.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    /** Paginated list with optional search. */
    Page<Category> getCategories(String query, int page);

    /** Get all active categories (for dropdowns). */
    List<Category> findAll();

    Category findById(Long id);

    /** Create (id == null) or update (id != null). */
    void save(Long id, String name, String description);

    void toggleStatus(Long id);
}
