package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.ProfileResponse;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ProfileService profileService;

    @ModelAttribute("profileInfo")
    public ProfileResponse populateProfileInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            User user = (User) session.getAttribute("loggedInUser");
            try {
                return profileService.getProfile(user.getId());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
