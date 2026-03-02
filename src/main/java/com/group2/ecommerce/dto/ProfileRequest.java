package com.group2.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    private String fullName;
    private String email;
    private String userPhone;
    private String recipientName;
    private String addressPhone;
    private String addressDetail;
}
