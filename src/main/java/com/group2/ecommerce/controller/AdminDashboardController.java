package com.group2.ecommerce.controller;

import com.group2.ecommerce.service.CouponService;
import com.group2.ecommerce.service.OrderService;
import com.group2.ecommerce.service.ProductService;
import com.group2.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CouponService couponService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {

        model.addAttribute("totalUsers",    userService.countAll());
        model.addAttribute("totalProducts", productService.countAll());
        model.addAttribute("totalOrders",   orderService.countAll());
        model.addAttribute("totalCoupons",  couponService.countAll());
        model.addAttribute("totalRevenue",  orderService.sumRevenue());
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }
}
