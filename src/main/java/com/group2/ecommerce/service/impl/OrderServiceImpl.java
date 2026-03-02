package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.enums.OrderStatus;
import com.group2.ecommerce.repository.OrderRepository;
import com.group2.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public long countAll() {
        return orderRepository.count();
    }

    @Override
    public BigDecimal sumRevenue() {
        BigDecimal revenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}
