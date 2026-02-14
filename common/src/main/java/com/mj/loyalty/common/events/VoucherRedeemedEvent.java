package com.mj.loyalty.common.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoucherRedeemedEvent extends BaseEvent {
    private String voucherId;
    private String userId;
    private String orderId;
    private BigDecimal discountAmount;
    
    public VoucherRedeemedEvent(String eventId, String voucherId, String userId, String orderId, BigDecimal discountAmount) {
        super(eventId, java.time.LocalDateTime.now(), "VoucherRedeemed");
        this.voucherId = voucherId;
        this.userId = userId;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
    }
}