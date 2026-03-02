package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.entity.Coupon;
import com.group2.ecommerce.repository.CouponRepository;
import com.group2.ecommerce.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public long countAll() {
        return couponRepository.count();
    }
}
