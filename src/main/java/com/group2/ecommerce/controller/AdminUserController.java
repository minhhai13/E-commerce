package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.user.UserRequest;
import com.group2.ecommerce.dto.user.UserResponse;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // ─── List ────────────────────────────────
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        q=q.trim();
        Page<UserResponse> usersPage = userService.getUsers(q, page);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("q", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "users");
        return "admin/users/user-list";
    }

    // ─── Form (Create & Edit) ─────────────────
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        UserRequest request = new UserRequest();
        if (id != null) {
            // Edit: pre-fill form with existing data
            UserResponse user = userService.findById(id);
            request.setFullName(user.getFullName());
            request.setEmail(user.getEmail());
            request.setPhone(user.getPhone());
            request.setRole(user.getRole());
        }
        model.addAttribute("userId", id);
        model.addAttribute("userRequest", request);
        model.addAttribute("roles", Role.values());
        model.addAttribute("activePage", "users");
        model.addAttribute("formTitle", id == null ? "Tạo người dùng mới" : "Chỉnh sửa người dùng #" + id);
        return "admin/users/user-form";
    }

    // ─── Save (Create or Update) ──────────────
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long userId,
                       @Valid @ModelAttribute("userRequest") UserRequest request,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("roles", Role.values());
            model.addAttribute("activePage", "users");
            model.addAttribute("formTitle", userId == null ? "Tạo người dùng mới" : "Chỉnh sửa người dùng #" + userId);
            return "admin/users/user-form";
        }
        try {
            userService.save(userId, request);  // null → create, non-null → update
            ra.addFlashAttribute("success", userId == null ? "Tạo người dùng thành công." : "Cập nhật người dùng thành công.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("roles", Role.values());
            model.addAttribute("activePage", "users");
            model.addAttribute("formTitle", userId == null ? "Tạo người dùng mới" : "Chỉnh sửa người dùng #" + userId);
            return "admin/users/user-form";
        }
        return "redirect:/admin/users";
    }

    // ─── Toggle Status ───────────────────────
    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id,
                               @RequestParam(defaultValue = "") String q,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes ra) {
        try {
            userService.toggleStatus(id);
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/users?q=" + q + "&page=" + page;
    }
}
