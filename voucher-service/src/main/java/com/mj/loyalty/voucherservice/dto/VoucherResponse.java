package com.mj.loyalty.voucherservice.dto;

import com.mj.loyalty.voucherservice.enums.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
    private String voucherId;
    private String userId;
    private String voucherCode;
    private VoucherStatus status;
    private BigDecimal discountAmount;
    private LocalDateTime expirationDate;
    private String description;
    private String orderId;
}