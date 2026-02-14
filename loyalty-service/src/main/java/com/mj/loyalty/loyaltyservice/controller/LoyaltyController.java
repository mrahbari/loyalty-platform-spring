package com.mj.loyalty.loyaltyservice.controller;

import com.mj.loyalty.loyaltyservice.dto.EarnPointsRequest;
import com.mj.loyalty.loyaltyservice.dto.LoyaltyAccountResponse;
import com.mj.loyalty.loyaltyservice.dto.RedeemPointsRequest;
import com.mj.loyalty.loyaltyservice.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @PostMapping("/earn")
    public ResponseEntity<LoyaltyAccountResponse> earnPoints(@Valid @RequestBody EarnPointsRequest request) {
        log.info("Earning {} points for user: {}", request.getPoints(), request.getUserId());
        LoyaltyAccountResponse response = loyaltyService.earnPoints(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem")
    public ResponseEntity<LoyaltyAccountResponse> redeemPoints(@Valid @RequestBody RedeemPointsRequest request) {
        log.info("Redeeming {} points for user: {}", request.getPoints(), request.getUserId());
        LoyaltyAccountResponse response = loyaltyService.redeemPoints(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{userId}")
    public ResponseEntity<LoyaltyAccountResponse> getLoyaltyAccount(@PathVariable String userId) {
        log.info("Fetching loyalty account for user: {}", userId);
        LoyaltyAccountResponse response = loyaltyService.getLoyaltyAccount(userId);
        return ResponseEntity.ok(response);
    }
}