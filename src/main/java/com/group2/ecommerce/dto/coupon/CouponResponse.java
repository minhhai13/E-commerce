package com.group2.ecommerce.dto.coupon;

import com.group2.ecommerce.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO returned to the admin UI with coupon information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer usageLimit;
    private int usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
}