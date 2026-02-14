package com.mj.loyalty.common.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoucherIssuedEvent extends BaseEvent {
    private String voucherId;
    private String userId;
    private String voucherCode;
    private BigDecimal discountAmount;
    private String expirationDate;
    
    public VoucherIssuedEvent(String eventId, String voucherId, String userId, String voucherCode, BigDecimal discountAmount, String expirationDate) {
        super(eventId, java.time.LocalDateTime.now(), "VoucherIssued");
        this.voucherId = voucherId;
        this.userId = userId;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
    }
}