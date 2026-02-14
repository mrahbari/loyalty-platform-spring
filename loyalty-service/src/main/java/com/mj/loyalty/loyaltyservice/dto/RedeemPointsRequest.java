package com.mj.loyalty.loyaltyservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedeemPointsRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @DecimalMin(value = "0.01", message = "Points must be greater than 0")
    @NotNull(message = "Points amount is required")
    private BigDecimal points;
    
    @NotBlank(message = "Reference ID is required")
    private String referenceId; // Reference to the order, purchase, etc.
    
    private String reason;
}