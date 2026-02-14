package com.mj.loyalty.loyaltyservice.entity;

import com.mj.loyalty.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "points_transactions")
@Getter
@Setter
public class PointsTransaction extends BaseEntity {
    @Column(nullable = false)
    private String userId;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // EARN or REDEEM
    
    @Column(nullable = false)
    private String referenceId; // Reference to the order, voucher, etc.
    
    @Column
    private String description;
    
    @Column(unique = true) // To prevent duplicate processing
    private String eventId; // For idempotency
    
    @Version
    private Integer version; // For optimistic locking
}