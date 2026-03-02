package com.group2.ecommerce.entity.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN(1),
    STAFF(2),
    CUSTOMER(3);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown Role value: " + value);
    }
}
