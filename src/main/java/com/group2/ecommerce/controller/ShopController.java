package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String showShopPage(
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        // Fetch the selected category and its hierarchy
        Category selectedCategory = null;
        if (categoryId != null) {
            selectedCategory = categoryRepository.findById(categoryId).orElse(null);
        }
        model.addAttribute("selectedCategory", selectedCategory);

        // Fetch categories for sidebar
        List<Category> categories;
        if (selectedCategory != null) {
            // If selected category is a parent, show its children
            // If it's a child, show its siblings and keep reference to parent
            if (selectedCategory.getParent() == null) {
                categories = selectedCategory.getChildren();
            } else {
                categories = selectedCategory.getParent().getChildren();
            }
        } else {
            categories = categoryRepository.findActiveRootCategories();
        }
        model.addAttribute("categories", categories);

        // Setup Sorting
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "id"); // Default "newest"
        if ("price_asc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "price");
        }

        // Create Pageable (9 items per page)
        int pageSize = 9;
        Pageable pageable = PageRequest.of(page, pageSize, sortOrder);

        // Clean keyword
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        // Fetch Products using Pageable
        Page<Product> productPage = productRepository.findByCategoryTreeAndKeyword(categoryId, searchKeyword, pageable);

        // Pass variables to view
        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentCategoryId", categoryId);
        model.addAttribute("currentKeyword", searchKeyword);
        model.addAttribute("currentSort", sort);

        return "shop";
    }
}
