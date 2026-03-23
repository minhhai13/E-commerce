package com.group2.ecommerce.service;

import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.enums.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    long countAll();
    // Thêm hàm để xử lý việc lưu đơn hàng vào DB
    Order createOrder(Long userId, Long addressId, String couponCode, HttpSession session);
    BigDecimal sumRevenue();
    List<Order> fetchOrderList();

    Order findOrderById(Long id);

    boolean advanceOrderStatus(Long id);

    boolean cancelOrder(Long id);

    long countOrderByStatus(OrderStatus status);

    List<Order> findOrderByStatus(OrderStatus status);

}
