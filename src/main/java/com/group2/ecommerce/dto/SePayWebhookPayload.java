package com.group2.ecommerce.dto;

import lombok.Data;

@Data
public class SePayWebhookPayload {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private Long transferAmount;
    private Long accumulated;
    private String code;
    private String content;
    private String transferType;
    private String referenceCode;
    private String description;
}
