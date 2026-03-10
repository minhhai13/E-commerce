package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.service.ProfileService;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import com.group2.ecommerce.entity.Product;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    // Hardcoded for demonstration. In production, use ContextHolder
    private static final Long MOCK_USER_ID = 1L;

    @GetMapping({ "/", "/home" })
    public String showHomePage(Model model) {

        try {
            // Include profile mock info so the shared dynamic navigation header works
            ProfileResponse profile = profileService.getProfile(MOCK_USER_ID);
            model.addAttribute("profileInfo", profile);
        } catch (Exception e) {
            // Ignored if user not found, header will fall back to default
        }

        // Fetch display data for the homepage
        model.addAttribute("categories", categoryRepository.findActiveRootCategories());
        // Fetch top 8 Products
        List<Product> products = productRepository
                .findAllByIsActiveTrue(org.springframework.data.domain.PageRequest.of(0, 8)).getContent();
        model.addAttribute("products", products);

        return "home";
    }
}
