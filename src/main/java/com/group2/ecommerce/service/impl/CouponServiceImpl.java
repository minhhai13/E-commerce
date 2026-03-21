package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.coupon.CouponRequest;
import com.group2.ecommerce.dto.coupon.CouponResponse;
import com.group2.ecommerce.entity.Coupon;
import com.group2.ecommerce.entity.enums.DiscountType;
import com.group2.ecommerce.repository.CouponRepository;
import com.group2.ecommerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {


    private static final int PAGE_SIZE = 10;

    private final CouponRepository couponRepository;

    // ─────────────── Mapping helpers ───────────────
    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .usageLimit(c.getUsageLimit())
                .usedCount(c.getUsedCount())
                .validFrom(c.getValidFrom())
                .validUntil(c.getValidUntil())
                .active(c.isActive())
                .build();
    }

    private Coupon findEntityById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found: " + id));
    }

    // ─────────────── Queries ───────────────
    @Override
    public Page<CouponResponse> getCoupons(String query, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        Page<Coupon> coupons = (query != null && !query.isBlank())
                ? couponRepository.findByCodeContainingIgnoreCase(query, pageable)
                : couponRepository.findAll(pageable);
        return coupons.map(this::toResponse);
    }

    @Override
    public CouponResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    // ─────────────── Commands ────────────────
    @Override
    @Transactional
    public void save(Long id, CouponRequest request) {
        // ensure valid date range
        if (request.getValidFrom().isAfter(request.getValidUntil())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        // ensure percentage discount does not exceed 100%
        if (request.getDiscountType() == DiscountType.PERCENTAGE && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage discount value cannot exceed 100%.");
        }

        if (id == null) {
            // create new
            if (couponRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("Coupon code already exists.");
            }
            Coupon coupon = Coupon.builder()
                    .code(request.getCode())
                    .discountType(request.getDiscountType())
                    .discountValue(request.getDiscountValue())
                    .usageLimit(request.getUsageLimit())
                    .usedCount(0)
                    .validFrom(request.getValidFrom())
                    .validUntil(request.getValidUntil())
                    .isActive(request.getIsActive() == null ? true : request.getIsActive())
                    .build();
            couponRepository.save(coupon);
        } else {
            // update existing
            if (couponRepository.existsByCodeAndIdNot(request.getCode(), id)) {
                throw new IllegalArgumentException("Coupon code already exists.");
            }
            Coupon coupon = findEntityById(id);
            coupon.setCode(request.getCode());
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setUsageLimit(request.getUsageLimit());
            coupon.setValidFrom(request.getValidFrom());
            coupon.setValidUntil(request.getValidUntil());
            if (request.getIsActive() != null) {
                coupon.setActive(request.getIsActive());
            }
            couponRepository.save(coupon);
        }
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Coupon coupon = findEntityById(id);
        coupon.setActive(!coupon.isActive());
        couponRepository.save(coupon);
    }

    @Override
    public long countAll() {
        return couponRepository.count();
    }

    @Override
    public CouponResponse validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Coupon code does not exist."));

        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon code has been disabled.");
        }

        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("Coupon has reached its usage limit.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom())) {
            throw new IllegalArgumentException("Coupon is not valid yet.");
        }
        if (now.isAfter(coupon.getValidUntil())) {
            throw new IllegalArgumentException("Coupon has expired.");
        }

        return toResponse(coupon);
    }
}