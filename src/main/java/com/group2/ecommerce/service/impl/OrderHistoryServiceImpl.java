package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.OrderHistoryResponse;
import com.group2.ecommerce.dto.OrderItemResponse;
import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.OrderItem;
import com.group2.ecommerce.repository.OrderItemRepository;
import com.group2.ecommerce.repository.OrderRepository;
import com.group2.ecommerce.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderHistoryResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

            List<OrderItemResponse> itemDtos = items.stream()
                    .map(item -> OrderItemResponse.builder()
                            .id(item.getId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .build())
                    .collect(Collectors.toList());

            return OrderHistoryResponse.builder()
                    .id(order.getId())
                    .status(order.getStatus())
                    .subtotal(order.getSubtotal())
                    .discountAmount(order.getDiscountAmount())
                    .totalAmount(order.getTotalAmount())
                    .shippingName(order.getShippingName())
                    .shippingPhone(order.getShippingPhone())
                    .shippingAddress(order.getShippingAddress())
                    .createdAt(order.getCreatedAt())
                    .items(itemDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public OrderHistoryResponse getOrderDetails(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderItemResponse> itemDtos = items.stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderHistoryResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}
