package com.mj.loyalty.voucherservice.dto;

import com.mj.loyalty.voucherservice.enums.VoucherStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVoucherRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @DecimalMin(value = "0.01", message = "Discount amount must be greater than 0")
    @NotNull(message = "Discount amount is required")
    private BigDecimal discountAmount;
    
    @Future(message = "Expiration date must be in the future")
    @NotNull(message = "Expiration date is required")
    private LocalDateTime expirationDate;
    
    private String description;
}