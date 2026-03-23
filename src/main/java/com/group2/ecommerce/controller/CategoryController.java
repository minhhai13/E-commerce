package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public String showCategoriesPage(Model model) {

        // Fetch root categories (parent is null) to show on the categories landing page
        List<Category> rootCategories = categoryRepository.findActiveRootCategories();
        model.addAttribute("categories", rootCategories);

        return "categories";
    }

    @GetMapping("/{id}")
    public String redirectCategoryToShop(@PathVariable("id") Long id) {
        // As requested: "Click vào category... Trang này sẽ hiển thị: Products in
        // category"
        // The most robust way without duplicating the entire complex shop.html logic is
        // to redirect to the shop endpoint with the category ID pre-selected.
        return "redirect:/shop?category=" + id;
    }
}
