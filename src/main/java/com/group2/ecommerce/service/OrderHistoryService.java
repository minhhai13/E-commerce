package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.OrderHistoryResponse;

import java.util.List;

public interface OrderHistoryService {
    List<OrderHistoryResponse> getOrdersByUserId(Long userId);

    OrderHistoryResponse getOrderDetails(Long orderId, Long userId);
}
