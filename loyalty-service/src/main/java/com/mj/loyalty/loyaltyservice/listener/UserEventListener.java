package com.mj.loyalty.loyaltyservice.listener;

import com.mj.loyalty.common.events.UserRegisteredEvent;
import com.mj.loyalty.loyaltyservice.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final LoyaltyService loyaltyService;

    @KafkaListener(topics = "UserRegistered", groupId = "loyalty-service-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for user: {}", event.getUserId());
        
        // Create a loyalty account for the new user with a welcome bonus
        // In a real system, you might want to configure the welcome bonus amount
        var request = new com.mj.loyalty.loyaltyservice.dto.EarnPointsRequest();
        request.setUserId(event.getUserId());
        request.setPoints(java.math.BigDecimal.valueOf(100)); // Welcome bonus
        request.setReferenceId(event.getEventId());
        request.setReason("Welcome bonus for new user registration");
        
        try {
            loyaltyService.earnPoints(request);
            log.info("Loyalty account created for user: {} with welcome bonus", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to create loyalty account for user: {}", event.getUserId(), e);
            // In a real system, you might want to implement retry logic or dead letter queue
            throw e;
        }
    }
}