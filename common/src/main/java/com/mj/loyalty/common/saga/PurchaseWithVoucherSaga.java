package com.mj.loyalty.common.saga;

import com.mj.loyalty.common.events.VoucherRedeemedEvent;
import com.mj.loyalty.common.service.OutboxService;
import com.mj.loyalty.voucherservice.dto.RedeemVoucherRequest;
import com.mj.loyalty.voucherservice.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseWithVoucherSaga {

    private final VoucherService voucherService;
    private final OutboxService outboxService;

    @Transactional
    public PurchaseWithVoucherResult execute(PurchaseWithVoucherCommand command) {
        log.info("Starting PurchaseWithVoucherSaga for order: {}", command.getOrderId());
        
        try {
            // Step 1: Redeem the voucher
            RedeemVoucherRequest redeemRequest = new RedeemVoucherRequest();
            redeemRequest.setVoucherCode(command.getVoucherCode());
            redeemRequest.setUserId(command.getUserId());
            redeemRequest.setOrderId(command.getOrderId());
            
            var voucherResponse = voucherService.redeemVoucher(redeemRequest);
            
            // Calculate final amount after discount
            BigDecimal finalAmount = command.getOrderAmount().subtract(voucherResponse.getDiscountAmount());
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO; // Don't allow negative amounts
            }
            
            // Create successful result
            var result = new PurchaseWithVoucherResult(
                command.getOrderId(),
                command.getUserId(),
                command.getOrderAmount(),
                voucherResponse.getDiscountAmount(),
                finalAmount,
                command.getVoucherCode(),
                true,
                null
            );
            
            log.info("PurchaseWithVoucherSaga completed successfully for order: {}", command.getOrderId());
            return result;
            
        } catch (Exception e) {
            log.error("PurchaseWithVoucherSaga failed for order: {}", command.getOrderId(), e);
            
            // In a real saga implementation, we would trigger compensation steps here
            // For example, if points were awarded, we would deduct them back
            
            return new PurchaseWithVoucherResult(
                command.getOrderId(),
                command.getUserId(),
                command.getOrderAmount(),
                BigDecimal.ZERO,
                command.getOrderAmount(),
                command.getVoucherCode(),
                false,
                e.getMessage()
            );
        }
    }
}