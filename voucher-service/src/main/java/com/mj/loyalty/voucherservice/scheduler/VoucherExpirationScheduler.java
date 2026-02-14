package com.mj.loyalty.voucherservice.scheduler;

import com.mj.loyalty.voucherservice.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherExpirationScheduler {

    private final VoucherService voucherService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void expireVouchers() {
        log.info("Starting voucher expiration process");
        try {
            voucherService.expireVouchers();
            log.info("Voucher expiration process completed");
        } catch (Exception e) {
            log.error("Error during voucher expiration process", e);
        }
    }
}