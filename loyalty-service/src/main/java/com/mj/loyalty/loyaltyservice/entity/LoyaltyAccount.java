package com.mj.loyalty.loyaltyservice.entity;

import com.mj.loyalty.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
public class LoyaltyAccount extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String userId;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal totalPoints = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal availablePoints = BigDecimal.ZERO;
    
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal spentPoints = BigDecimal.ZERO;
    
    @Version
    private Integer version; // For optimistic locking
}