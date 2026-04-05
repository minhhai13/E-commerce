package com.group2.ecommerce.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CANCELLED(0),
    WAITING_PAYMENT(1),
    WAITING_CONFIRMATION(2),
    WAITING_PICKUP(3),
    IN_TRANSIT(4),
    COMPLETED(5);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OrderStatus value: " + value);
    }
}
