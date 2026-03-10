package com.group2.ecommerce.dto;

import com.group2.ecommerce.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public String getStatusLabel() {
        if (status == null)
            return "Unknown";
        switch (status) {
            case CANCELLED:
                return "Cancelled";
            case WAITING_CONFIRMATION:
                return "Waiting Confirmation";
            case WAITING_PICKUP:
                return "Waiting Pickup";
            case IN_TRANSIT:
                return "In Transit";
            case COMPLETED:
                return "Completed";
            default:
                return "Unknown";
        }
    }

    public String getStatusCssClass() {
        if (status == null)
            return "badge-waiting";
        switch (status) {
            case CANCELLED:
                return "badge-cancelled";
            case WAITING_CONFIRMATION:
                return "badge-waiting";
            case WAITING_PICKUP:
                return "badge-pickup";
            case IN_TRANSIT:
                return "badge-shipping";
            case COMPLETED:
                return "badge-delivered";
            default:
                return "badge-waiting";
        }
    }
}
