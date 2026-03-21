package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.repository.CategoryRepository;
import com.group2.ecommerce.service.CartService;
import com.group2.ecommerce.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    @ModelAttribute("allParentCategories")
    public List<Category> getAllParentCategories() {
        return categoryRepository.findActiveRootCategories();
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpSession session) {
        return cartService.getCartItemCount(session);
    }

    @ModelAttribute("profileInfo")
    public ProfileResponse getProfileInfo(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            try {
                return profileService.getProfile(user.getId());
            } catch (Exception e) {
                // Return null if profile loading fails
                return null;
            }
        }
        return null;
    }
}
