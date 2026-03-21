package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.coupon.CouponRequest;
import com.group2.ecommerce.dto.coupon.CouponResponse;
import org.springframework.data.domain.Page;

public interface CouponService {
    long countAll();

    // Retrieves a pageable list of coupons, optionally filtering by code.
    Page<CouponResponse> getCoupons(String query, int page);

    CouponResponse findById(Long id);


    // Create new coupon or update existing when id is non-null
    void save(Long id, CouponRequest request);

    // Toggle active state (enable/disable)
    void toggleStatus(Long id);

    // Validate a coupon for checkout
    CouponResponse validateCoupon(String code);
}