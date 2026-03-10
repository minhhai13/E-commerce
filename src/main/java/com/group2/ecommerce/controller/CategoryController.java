package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.service.ProfileService;
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
    private final ProfileService profileService;

    private static final Long MOCK_USER_ID = 1L;

    @GetMapping
    public String showCategoriesPage(Model model) {
        try {
            // Profile mock info for layout
            ProfileResponse profile = profileService.getProfile(MOCK_USER_ID);
            model.addAttribute("profileInfo", profile);
        } catch (Exception e) {
            // ignore
        }

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
