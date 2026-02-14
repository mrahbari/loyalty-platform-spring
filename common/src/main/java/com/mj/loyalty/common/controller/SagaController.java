package com.mj.loyalty.common.controller;

import com.mj.loyalty.common.saga.PurchaseWithVoucherCommand;
import com.mj.loyalty.common.saga.PurchaseWithVoucherResult;
import com.mj.loyalty.common.saga.PurchaseWithVoucherSaga;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class SagaController {

    private final PurchaseWithVoucherSaga purchaseWithVoucherSaga;

    @PostMapping("/purchase-with-voucher")
    public ResponseEntity<PurchaseWithVoucherResult> executePurchaseWithVoucher(@Valid @RequestBody PurchaseWithVoucherCommand command) {
        log.info("Executing PurchaseWithVoucherSaga for order: {}", command.getOrderId());
        PurchaseWithVoucherResult result = purchaseWithVoucherSaga.execute(command);
        return ResponseEntity.ok(result);
    }
}