package com.mj.loyalty.voucherservice.entity;

import com.mj.loyalty.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
public class Voucher extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String voucherId;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(unique = true, nullable = false)
    private String voucherCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherStatus status = VoucherStatus.ACTIVE;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal discountAmount;
    
    @Column(nullable = false)
    private LocalDateTime expirationDate;
    
    @Column
    private String description;
    
    @Column
    private String orderId; // The order that triggered this voucher
    
    @Version
    private Integer version; // For optimistic locking
}