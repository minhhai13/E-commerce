package com.group2.ecommerce.service;

import com.group2.ecommerce.entity.Order;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface OrderService {

    long countAll();

    BigDecimal sumRevenue();
}
