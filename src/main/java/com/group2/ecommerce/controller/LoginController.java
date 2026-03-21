package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.auth.LoginRequest;
import com.group2.ecommerce.dto.auth.RegisterRequest;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            return redirectByRole(user.getRole());
        }
        // Thêm DTO vào model để ràng buộc với form
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        Optional<User> userOpt = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }

        User user = userOpt.get();
        session.setAttribute("loggedInUser", user);
        return redirectByRole(user.getRole());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result,
                           RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(request);
            ra.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            ra.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        return "error/403";
    }

    private String redirectByRole(Role role) {
        return switch (role) {
            case ADMIN -> "redirect:/admin/dashboard";
            case STAFF -> "redirect:/staff/dashboard"; /* adjust as needed */
            case CUSTOMER -> "redirect:/home"; /* adjust as needed */
            default -> "redirect:/login";
        };
    }
}
