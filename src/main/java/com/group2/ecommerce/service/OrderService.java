package com.group2.ecommerce.service;

import com.group2.ecommerce.entity.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface OrderService {

    long countAll();
    // Thêm hàm để xử lý việc lưu đơn hàng vào DB
    Order createOrder(Long userId, Long addressId, String couponCode, HttpSession session);
    BigDecimal sumRevenue();
}
