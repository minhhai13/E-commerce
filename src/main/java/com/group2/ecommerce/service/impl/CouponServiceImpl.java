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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy coupon: " + id));
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
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc.");
        }

        // ensure percentage discount does not exceed 100%
        if (request.getDiscountType() == DiscountType.PERCENTAGE && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Giá trị giảm giá theo phần trăm không được vượt quá 100%.");
        }

        if (id == null) {
            // create new
            if (couponRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("Mã coupon đã tồn tại.");
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
                throw new IllegalArgumentException("Mã coupon đã tồn tại.");
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
}