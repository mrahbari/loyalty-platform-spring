package com.mj.loyalty.voucherservice.controller;

import com.mj.loyalty.voucherservice.dto.CreateVoucherRequest;
import com.mj.loyalty.voucherservice.dto.RedeemVoucherRequest;
import com.mj.loyalty.voucherservice.dto.VoucherResponse;
import com.mj.loyalty.voucherservice.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<VoucherResponse> createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
        log.info("Creating voucher for user: {}", request.getUserId());
        VoucherResponse response = voucherService.createVoucher(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem")
    public ResponseEntity<VoucherResponse> redeemVoucher(@Valid @RequestBody RedeemVoucherRequest request) {
        log.info("Redeeming voucher with code: {} for user: {}", request.getVoucherCode(), request.getUserId());
        VoucherResponse response = voucherService.redeemVoucher(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> getVoucherById(@PathVariable String voucherId) {
        log.info("Fetching voucher with ID: {}", voucherId);
        VoucherResponse response = voucherService.getVoucherById(voucherId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VoucherResponse>> getVouchersByUserId(@PathVariable String userId) {
        log.info("Fetching vouchers for user: {}", userId);
        List<VoucherResponse> response = voucherService.getVouchersByUserId(userId);
        return ResponseEntity.ok(response);
    }
}