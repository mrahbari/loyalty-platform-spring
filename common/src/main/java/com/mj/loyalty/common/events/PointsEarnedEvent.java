package com.mj.loyalty.common.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class PointsEarnedEvent extends BaseEvent {
    private String userId;
    private String referenceId;
    private BigDecimal points;
    private String reason;
    
    public PointsEarnedEvent(String eventId, String userId, String referenceId, BigDecimal points, String reason) {
        super(eventId, java.time.LocalDateTime.now(), "PointsEarned");
        this.userId = userId;
        this.referenceId = referenceId;
        this.points = points;
        this.reason = reason;
    }
}