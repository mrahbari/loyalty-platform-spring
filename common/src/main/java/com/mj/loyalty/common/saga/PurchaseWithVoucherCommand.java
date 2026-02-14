package com.mj.loyalty.common.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseWithVoucherCommand {
    private String userId;
    private String orderId;
    private BigDecimal orderAmount;
    private String voucherCode;
}