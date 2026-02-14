package com.mj.loyalty.voucherservice.service;

import com.mj.loyalty.common.events.VoucherIssuedEvent;
import com.mj.loyalty.common.events.VoucherRedeemedEvent;
import com.mj.loyalty.common.service.OutboxService;
import com.mj.loyalty.voucherservice.dto.CreateVoucherRequest;
import com.mj.loyalty.voucherservice.dto.RedeemVoucherRequest;
import com.mj.loyalty.voucherservice.dto.VoucherResponse;
import com.mj.loyalty.voucherservice.entity.Voucher;
import com.mj.loyalty.voucherservice.enums.VoucherStatus;
import com.mj.loyalty.voucherservice.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final OutboxService outboxService;

    @Transactional
    public VoucherResponse createVoucher(CreateVoucherRequest request) {
        // Generate unique voucher code
        String voucherCode = generateVoucherCode();

        Voucher voucher = new Voucher();
        voucher.setVoucherId(UUID.randomUUID().toString());
        voucher.setUserId(request.getUserId());
        voucher.setVoucherCode(voucherCode);
        voucher.setDiscountAmount(request.getDiscountAmount());
        voucher.setExpirationDate(request.getExpirationDate());
        voucher.setDescription(request.getDescription());

        Voucher savedVoucher = voucherRepository.save(voucher);

        // Publish VoucherIssuedEvent
        VoucherIssuedEvent event = new VoucherIssuedEvent(
            UUID.randomUUID().toString(),
            savedVoucher.getVoucherId(),
            savedVoucher.getUserId(),
            savedVoucher.getVoucherCode(),
            savedVoucher.getDiscountAmount(),
            savedVoucher.getExpirationDate().toString()
        );
        
        outboxService.saveEvent("Voucher", savedVoucher.getVoucherId(), "VoucherIssued", event);

        log.info("Voucher created with ID: {}", savedVoucher.getVoucherId());
        
        return mapToResponse(savedVoucher);
    }

    @Transactional
    public VoucherResponse redeemVoucher(RedeemVoucherRequest request) {
        // Find voucher by code
        Voucher voucher = voucherRepository.findByVoucherCode(request.getVoucherCode())
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found with code: " + request.getVoucherCode()));

        // Validate user
        if (!voucher.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Voucher does not belong to user: " + request.getUserId());
        }

        // Validate status
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new IllegalArgumentException("Voucher is not active. Current status: " + voucher.getStatus());
        }

        // Validate expiration
        if (voucher.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Voucher has expired");
        }

        // Attempt to update status atomically to prevent race conditions
        int rowsUpdated = voucherRepository.updateVoucherStatus(voucher.getVoucherId(), VoucherStatus.USED);
        
        if (rowsUpdated == 0) {
            throw new IllegalStateException("Failed to update voucher status - possibly already used");
        }

        // Refresh the entity to get the updated status
        voucher.setStatus(VoucherStatus.USED);
        voucher.setOrderId(request.getOrderId());

        // Publish VoucherRedeemedEvent
        VoucherRedeemedEvent event = new VoucherRedeemedEvent(
            UUID.randomUUID().toString(),
            voucher.getVoucherId(),
            voucher.getUserId(),
            request.getOrderId(),
            voucher.getDiscountAmount()
        );
        
        outboxService.saveEvent("Voucher", voucher.getVoucherId(), "VoucherRedeemed", event);

        log.info("Voucher redeemed with ID: {}", voucher.getVoucherId());
        
        return mapToResponse(voucher);
    }

    public VoucherResponse getVoucherById(String voucherId) {
        Voucher voucher = voucherRepository.findByVoucherId(voucherId)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found with ID: " + voucherId));
        
        return mapToResponse(voucher);
    }

    public List<VoucherResponse> getVouchersByUserId(String userId) {
        List<Voucher> vouchers = voucherRepository.findByUserId(userId);
        return vouchers.stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public void expireVouchers() {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> expiredVouchers = voucherRepository.findExpiredVouchers(now);
        
        for (Voucher voucher : expiredVouchers) {
            if (voucher.getStatus() == VoucherStatus.ACTIVE) {
                voucher.setStatus(VoucherStatus.EXPIRED);
                voucherRepository.save(voucher);
                
                log.info("Voucher expired: {}", voucher.getVoucherId());
            }
        }
    }

    private String generateVoucherCode() {
        // Generate a unique voucher code (in a real system, you might want more sophisticated generation)
        return "VOUCHER_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private VoucherResponse mapToResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setVoucherId(voucher.getVoucherId());
        response.setUserId(voucher.getUserId());
        response.setVoucherCode(voucher.getVoucherCode());
        response.setStatus(voucher.getStatus());
        response.setDiscountAmount(voucher.getDiscountAmount());
        response.setExpirationDate(voucher.getExpirationDate());
        response.setDescription(voucher.getDescription());
        response.setOrderId(voucher.getOrderId());
        return response;
    }
}