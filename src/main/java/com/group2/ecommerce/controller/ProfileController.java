package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.ProfileRequest;
import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // Hardcoded for demonstration, typically obtained from SecurityContext or
    // Session
    private final Long MOCK_USER_ID = 1L;

    @GetMapping
    public String showProfileForm(Model model) {
        try {
            ProfileResponse profile = profileService.getProfile(MOCK_USER_ID);

            ProfileRequest request = ProfileRequest.builder()
                    .fullName(profile.getFullName())
                    .email(profile.getEmail())
                    .userPhone(profile.getUserPhone())
                    .recipientName(profile.getRecipientName())
                    .addressPhone(profile.getAddressPhone())
                    .addressDetail(profile.getAddressDetail())
                    .build();

            model.addAttribute("profile", request);
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            model.addAttribute("profile", new ProfileRequest());
            return "profile";
        }
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("profile") ProfileRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            profileService.updateProfile(MOCK_USER_ID, request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
