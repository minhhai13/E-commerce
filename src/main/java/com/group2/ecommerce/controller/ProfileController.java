package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.AddressRequest;
import com.group2.ecommerce.dto.PasswordRequest;
import com.group2.ecommerce.dto.ProfileRequest;
import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.service.AddressService;
import com.group2.ecommerce.dto.OrderHistoryResponse;
import com.group2.ecommerce.service.OrderHistoryService;
import com.group2.ecommerce.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final OrderHistoryService orderHistoryService;
    private final AddressService addressService;
    private final jakarta.servlet.http.HttpSession session;

    private Long getCurrentUserId() {
        com.group2.ecommerce.entity.User user = (com.group2.ecommerce.entity.User) session.getAttribute("loggedInUser");
        if (user != null) return user.getId();
        throw new IllegalStateException("User not logged in");
    }

    // ─── GET /profile ─── Show profile tab
    @GetMapping
    public String showProfile(Model model) {
        return loadModel(model, "profile");
    }

    // ─── GET /profile/orders ─── Show order history tab
    @GetMapping("/orders")
    public String showOrders(Model model) {
        return loadModel(model, "orders");
    }

    // ─── GET /profile/orders/{id} ─── Show order details
    @GetMapping("/orders/{id}")
    public String showOrderDetails(@PathVariable("id") Long id, Model model) {
        try {
            OrderHistoryResponse orderDetails = orderHistoryService.getOrderDetails(id, getCurrentUserId());
            model.addAttribute("orderInfo", orderDetails);
            model.addAttribute("activeTab", "orders");

            // Add profile info so the sidebar can render correctly
            ProfileResponse profile = profileService.getProfile(getCurrentUserId());
            model.addAttribute("profileInfo", profile);

            return "order-detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading order: " + e.getMessage());
            return loadModel(model, "orders");
        }
    }

    // ─── GET /profile/addresses ─── Show saved address tab
    @GetMapping("/addresses")
    public String showAddresses(Model model) {
        return loadModel(model, "address");
    }

    // ─── GET /profile/security ─── Show security tab
    @GetMapping("/security")
    public String showSecurity(Model model) {
        return loadModel(model, "security");
    }

    // ─── POST /profile ─── Update personal info
    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return loadModel(model, "profile");
        }
        try {
            profileService.updateProfile(getCurrentUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            redirectAttributes.addFlashAttribute("activeTab", "profile");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return loadModel(model, "profile");
        }
        return "redirect:/profile";
    }

    // ─── POST /profile/addresses ─── Add a new address
    @PostMapping("/addresses")
    public String addAddress(@Valid @ModelAttribute("newAddress") AddressRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("showAddAddressForm", true);
            return loadModel(model, "address");
        }
        try {
            addressService.addAddress(getCurrentUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Address added successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("showAddAddressForm", true);
            return loadModel(model, "address");
        }
        redirectAttributes.addFlashAttribute("activeTab", "address");
        return "redirect:/profile/addresses";
    }

    // ─── POST /profile/addresses/{id}/edit ─── Edit an existing address
    @PostMapping("/addresses/{id}/edit")
    public String editAddress(@PathVariable("id") Long id,
            @Valid @ModelAttribute("editAddress") AddressRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("showEditAddressModal", id);
            return loadModel(model, "address");
        }
        try {
            addressService.updateAddress(getCurrentUserId(), id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Address updated!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("showEditAddressModal", id);
            return loadModel(model, "address");
        }
        redirectAttributes.addFlashAttribute("activeTab", "address");
        return "redirect:/profile/addresses";
    }

    // ─── POST /profile/addresses/{id}/delete ─── Delete an address
    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(@PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            addressService.deleteAddress(getCurrentUserId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "Address deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("activeTab", "address");
        return "redirect:/profile/addresses";
    }

    // ─── POST /profile/addresses/{id}/default ─── Set address as default
    @PostMapping("/addresses/{id}/default")
    public String setDefaultAddress(@PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            addressService.setDefaultAddress(getCurrentUserId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "Default address updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        redirectAttributes.addFlashAttribute("activeTab", "address");
        return "redirect:/profile/addresses";
    }

    // ─── POST /profile/security ─── Change password
    @PostMapping("/security")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return loadModel(model, "security");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.passwordForm", "Passwords do not match");
            return loadModel(model, "security");
        }
        try {
            profileService.changePassword(getCurrentUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return loadModel(model, "security");
        }
        redirectAttributes.addFlashAttribute("activeTab", "security");
        return "redirect:/profile/security";
    }

    // ─── Helper: build model for all tabs ───
    private String loadModel(Model model, String activeTab) {
        try {
            ProfileResponse profile = profileService.getProfile(getCurrentUserId());

            ProfileRequest profileForm = ProfileRequest.builder()
                    .fullName(profile.getFullName())
                    .email(profile.getEmail())
                    .userPhone(profile.getUserPhone())
                    .recipientName(profile.getRecipientName())
                    .addressPhone(profile.getAddressPhone())
                    .addressDetail(profile.getAddressDetail())
                    .build();

            model.addAttribute("profileForm", model.containsAttribute("profileForm") ? model.asMap().get("profileForm") : profileForm);
            model.addAttribute("profileInfo", profile);
            model.addAttribute("orders", orderHistoryService.getOrdersByUserId(getCurrentUserId()));
            model.addAttribute("addresses", addressService.getAddressesByUserId(getCurrentUserId()));
            model.addAttribute("newAddress", model.containsAttribute("newAddress") ? model.asMap().get("newAddress") : new AddressRequest());
            model.addAttribute("editAddress", model.containsAttribute("editAddress") ? model.asMap().get("editAddress") : new AddressRequest());
            model.addAttribute("passwordForm", model.containsAttribute("passwordForm") ? model.asMap().get("passwordForm") : new PasswordRequest());
            model.addAttribute("activeTab", model.containsAttribute("activeTab")
                    ? model.asMap().get("activeTab")
                    : activeTab);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading data: " + e.getMessage());
            model.addAttribute("profileForm", new ProfileRequest());
            model.addAttribute("newAddress", new AddressRequest());
            model.addAttribute("passwordForm", new PasswordRequest());
            model.addAttribute("activeTab", activeTab);
        }
        return "profile";
    }
}
