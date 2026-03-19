package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final int PAGE_SIZE = 10;

    private final CategoryRepository categoryRepository;

    @Override
    public Page<Category> getCategories(String query, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        if (query != null && !query.isBlank()) {
            return categoryRepository.findByNameContainingIgnoreCase(query, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name").ascending());
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Override
    @Transactional
    public void save(Long id, String name, String description, Long parentId) {
        name = name == null ? "" : name.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        Category parent = null;
        if (parentId != null) {
            if (id != null && id.equals(parentId)) {
                throw new IllegalArgumentException("A category cannot be its own parent.");
            }
            parent = findById(parentId);

            if (id != null) {
                // Check for circular reference
                Category currentParent = parent.getParent();
                while (currentParent != null) {
                    if (currentParent.getId().equals(id)) {
                        throw new IllegalArgumentException("Cannot set a descendant category as parent (circular reference).");
                    }
                    currentParent = currentParent.getParent();
                }
            }
        }

        if (id == null) {
            // Create
            if (categoryRepository.existsByName(name)) {
                throw new IllegalArgumentException("Category name '" + name + "' already exists.");
            }
            Category category = Category.builder()
                    .name(name)
                    .description(description)
                    .parent(parent)
                    .isActive(true)
                    .build();
            categoryRepository.save(category);
        } else {
            // Update
            if (categoryRepository.existsByNameAndIdNot(name, id)) {
                throw new IllegalArgumentException("Category name '" + name + "' already exists.");
            }
            Category category = findById(id);
            category.setName(name);
            category.setDescription(description);
            category.setParent(parent);
            categoryRepository.save(category);
        }
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Category category = findById(id);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getCategoryAndDescendantIds(Long categoryId) {
        if (categoryId == null) return List.of();
        
        List<Long> ids = new java.util.ArrayList<>();
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            collectIds(category, ids);
        }
        return ids;
    }

    private void collectIds(Category category, List<Long> ids) {
        ids.add(category.getId());
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                collectIds(child, ids);
            }
        }
    }
}
