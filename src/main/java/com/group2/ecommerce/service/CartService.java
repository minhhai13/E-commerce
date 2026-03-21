package com.group2.ecommerce.service;

import com.group2.ecommerce.dto.CartItem;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    List<CartItem> getCart(HttpSession session);

    void addToCart(Long productId, int quantity, HttpSession session) throws Exception;

    void updateQuantity(Long productId, int quantity, HttpSession session) throws Exception;

    void removeFromCart(Long productId, HttpSession session);

    BigDecimal calculateTotal(List<CartItem> cart);

    int getCartItemCount(HttpSession session);
}
