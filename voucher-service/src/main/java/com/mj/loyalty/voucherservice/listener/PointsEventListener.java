package com.mj.loyalty.voucherservice.listener;

import com.mj.loyalty.common.events.PointsRedeemedEvent;
import com.mj.loyalty.voucherservice.dto.CreateVoucherRequest;
import com.mj.loyalty.voucherservice.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointsEventListener {

    private final VoucherService voucherService;

    @KafkaListener(topics = "PointsRedeemed", groupId = "voucher-service-group")
    public void handlePointsRedeemed(PointsRedeemedEvent event) {
        log.info("Received PointsRedeemedEvent for user: {}", event.getUserId());
        
        // Issue a voucher as a reward for spending points
        // In a real system, you might want to configure the conversion rate
        BigDecimal discountAmount = event.getPoints().multiply(BigDecimal.valueOf(0.1)); // 10% of points as discount
        
        var request = new CreateVoucherRequest();
        request.setUserId(event.getUserId());
        request.setDiscountAmount(discountAmount);
        request.setExpirationDate(LocalDateTime.now().plusDays(30)); // Valid for 30 days
        request.setDescription("Reward voucher for spending loyalty points");
        
        try {
            voucherService.createVoucher(request);
            log.info("Reward voucher created for user: {} after spending points", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to create reward voucher for user: {}", event.getUserId(), e);
            // In a real system, you might want to implement retry logic or dead letter queue
            throw e;
        }
    }
}