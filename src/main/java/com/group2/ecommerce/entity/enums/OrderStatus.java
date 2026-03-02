package com.group2.ecommerce.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CANCELLED(0),
    WAITING_CONFIRMATION(1),
    WAITING_PICKUP(2),
    IN_TRANSIT(3),
    COMPLETED(4);

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
