package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.coupon.CouponRequest;
import com.group2.ecommerce.dto.coupon.CouponResponse;
import com.group2.ecommerce.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    // ─── List ────────────────────────────────
    @GetMapping
    public String listCoupons(@RequestParam(defaultValue = "") String q,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        q = q.trim();
        Page<CouponResponse> couponsPage = couponService.getCoupons(q, page);
        model.addAttribute("couponsPage", couponsPage);
        model.addAttribute("q", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "coupons");
        return "admin/coupons/coupon-list";
    }

    // ─── Form (Create & Edit) ─────────────────
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        CouponRequest request = new CouponRequest();
        if (id != null) {
            CouponResponse coupon = couponService.findById(id);
            request.setCode(coupon.getCode());
            request.setDiscountType(coupon.getDiscountType());
            request.setDiscountValue(coupon.getDiscountValue());
            request.setUsageLimit(coupon.getUsageLimit());
            request.setValidFrom(coupon.getValidFrom());
            request.setValidUntil(coupon.getValidUntil());
            request.setIsActive(coupon.isActive());
        }
        model.addAttribute("couponId", id);
        model.addAttribute("couponRequest", request);
        model.addAttribute("discountTypes", com.group2.ecommerce.entity.enums.DiscountType.values());
        model.addAttribute("activePage", "coupons");
        model.addAttribute("formTitle", id == null ? "Tạo khuyến mãi mới" : "Chỉnh sửa khuyến mãi #" + id);
        return "admin/coupons/coupon-form";
    }

    // ─── Save (Create or Update) ──────────────
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long couponId,
                       @Valid @ModelAttribute("couponRequest") CouponRequest request,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("couponId", couponId);
            model.addAttribute("discountTypes", com.group2.ecommerce.entity.enums.DiscountType.values());
            model.addAttribute("activePage", "coupons");
            model.addAttribute("formTitle", couponId == null ? "Tạo khuyến mãi mới" : "Chỉnh sửa khuyến mãi #" + couponId);
            return "admin/coupons/coupon-form";
        }
        try {
            couponService.save(couponId, request);
            ra.addFlashAttribute("success", couponId == null ? "Tạo khuyến mãi thành công." : "Cập nhật khuyến mãi thành công.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("couponId", couponId);
            model.addAttribute("discountTypes", com.group2.ecommerce.entity.enums.DiscountType.values());
            model.addAttribute("activePage", "coupons");
            model.addAttribute("formTitle", couponId == null ? "Tạo khuyến mãi mới" : "Chỉnh sửa khuyến mãi #" + couponId);
            return "admin/coupons/coupon-form";
        }
        return "redirect:/admin/coupons";
    }

    // ─── Toggle Status ───────────────────────
    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id,
                               @RequestParam(defaultValue = "") String q,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes ra) {
        try {
            couponService.toggleStatus(id);
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/coupons?q=" + q + "&page=" + page;
    }
}