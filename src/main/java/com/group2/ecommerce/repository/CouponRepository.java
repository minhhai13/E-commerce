package com.group2.ecommerce.repository;

import com.group2.ecommerce.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // search coupons by code (supports simple query for admin list)
    Page<Coupon> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    // uniqueness checks used when creating/updating coupons
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<Coupon> findByCode(String code);
}