package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.dto.CartItem;
import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.repository.ProductRepository;
import com.group2.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    @SuppressWarnings("unchecked")
    @Override
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @Override
    public void addToCart(Long productId, int quantity, HttpSession session) throws Exception {
        if (quantity < 1) {
            throw new Exception("Quantity must be at least 1.");
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty() || !productOpt.get().isActive()) {
            throw new Exception("Product not found.");
        }

        Product product = productOpt.get();
        List<CartItem> cart = getCart(session);

        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        int currentQtyInCart = existingItem.map(CartItem::getQuantity).orElse(0);
        int newTotalQty = currentQtyInCart + quantity;

        if (product.getStockQuantity() <= 0) {
            throw new Exception("Sorry, this product is currently out of stock.");
        }

        if (newTotalQty > product.getStockQuantity()) {
            throw new Exception("Cannot add " + quantity + " item(s). Only " + product.getStockQuantity()
                    + " available in stock"
                    + (currentQtyInCart > 0 ? " (you already have " + currentQtyInCart + " in your cart)." : "."));
        }

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(newTotalQty);
        } else {
            cart.add(new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getImageName(),
                    product.getPrice(),
                    quantity,
                    product.getStockQuantity()
            ));
        }
        session.setAttribute("cart", cart);
    }

    @Override
    public void updateQuantity(Long productId, int quantity, HttpSession session) throws Exception {
        List<CartItem> cart = getCart(session);

        if (quantity < 1) {
            removeFromCart(productId, session);
            return;
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (quantity > product.getStockQuantity()) {
                throw new Exception("Cannot set quantity to " + quantity + ". Only " + product.getStockQuantity() + " available in stock.");
            }
        }

        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        session.setAttribute("cart", cart);
    }

    @Override
    public void removeFromCart(Long productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute("cart", cart);
    }

    @Override
    public BigDecimal calculateTotal(List<CartItem> cart) {
        return cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int getCartItemCount(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) return 0;
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }
}
