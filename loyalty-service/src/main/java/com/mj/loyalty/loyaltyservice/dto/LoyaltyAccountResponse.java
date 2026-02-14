package com.mj.loyalty.loyaltyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyAccountResponse {
    private String userId;
    private BigDecimal totalPoints;
    private BigDecimal availablePoints;
    private BigDecimal spentPoints;
}