package com.group2.ecommerce.service.impl;

import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.enums.OrderStatus;
import com.group2.ecommerce.repository.OrderRepository;
import com.group2.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import com.group2.ecommerce.dto.CartItem;
import com.group2.ecommerce.entity.Coupon;
import com.group2.ecommerce.entity.OrderItem;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.UserAddress;
import com.group2.ecommerce.entity.enums.DiscountType;
import com.group2.ecommerce.repository.CouponRepository;
import com.group2.ecommerce.repository.ProductRepository;
import com.group2.ecommerce.repository.UserAddressRepository;
import com.group2.ecommerce.repository.UserRepository;
import com.group2.ecommerce.service.CartService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    public long countAll() {
        return orderRepository.count();
    }

    @Override
    public BigDecimal sumRevenue() {
        BigDecimal revenue = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, String couponCode, HttpSession session) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));

        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ."));

        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Truy cập bị từ chối.");
        }

        List<CartItem> cartItems = cartService.getCart(session);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống.");
        }

        BigDecimal subtotal = cartService.calculateTotal(cartItems);
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon appliedCoupon = null;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            appliedCoupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không hợp lệ."));
            
            if (!appliedCoupon.isActive() || appliedCoupon.getUsedCount() >= appliedCoupon.getUsageLimit() ||
                java.time.LocalDateTime.now().isBefore(appliedCoupon.getValidFrom()) ||
                java.time.LocalDateTime.now().isAfter(appliedCoupon.getValidUntil())) {
                throw new IllegalArgumentException("Mã giảm giá không đủ điều kiện áp dụng.");
            }

            if (appliedCoupon.getDiscountType() == DiscountType.PERCENTAGE) {
                discountAmount = subtotal.multiply(appliedCoupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
            } else {
                discountAmount = appliedCoupon.getDiscountValue();
            }

            if (discountAmount.compareTo(subtotal) > 0) {
                discountAmount = subtotal;
            }
        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        Order order = Order.builder()
                .user(user)
                .coupon(appliedCoupon)
                .status(OrderStatus.WAITING_CONFIRMATION)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .shippingName(address.getRecipientName())
                .shippingPhone(address.getPhone())
                .shippingAddress(address.getAddressDetail())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            com.group2.ecommerce.entity.Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + item.getProductName()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Sản phẩm không đủ số lượng tồn kho: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(item.getPrice())
                    .quantity(item.getQuantity())
                    .build();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        if (appliedCoupon != null) {
            appliedCoupon.setUsedCount(appliedCoupon.getUsedCount() + 1);
            couponRepository.save(appliedCoupon);
        }

        // Clear the cart
        session.removeAttribute("cart");

        return savedOrder;
    }
}
