package com.mj.loyalty.loyaltyservice.service;

import com.mj.loyalty.common.events.PointsEarnedEvent;
import com.mj.loyalty.common.events.PointsRedeemedEvent;
import com.mj.loyalty.common.service.OutboxService;
import com.mj.loyalty.loyaltyservice.dto.EarnPointsRequest;
import com.mj.loyalty.loyaltyservice.dto.LoyaltyAccountResponse;
import com.mj.loyalty.loyaltyservice.dto.RedeemPointsRequest;
import com.mj.loyalty.loyaltyservice.entity.LoyaltyAccount;
import com.mj.loyalty.loyaltyservice.entity.PointsTransaction;
import com.mj.loyalty.loyaltyservice.enums.TransactionType;
import com.mj.loyalty.loyaltyservice.repository.LoyaltyAccountRepository;
import com.mj.loyalty.loyaltyservice.repository.PointsTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final OutboxService outboxService;

    @Transactional
    public LoyaltyAccountResponse earnPoints(EarnPointsRequest request) {
        // Check if transaction already exists (idempotency)
        if (pointsTransactionRepository.existsByEventId(request.getReferenceId())) {
            log.warn("Points already earned for reference ID: {}", request.getReferenceId());
            // Return current account state
            return getLoyaltyAccount(request.getUserId());
        }

        // Get or create loyalty account
        LoyaltyAccount account = loyaltyAccountRepository.findByUserId(request.getUserId())
            .orElse(createNewLoyaltyAccount(request.getUserId()));

        // Update account balances
        account.setTotalPoints(account.getTotalPoints().add(request.getPoints()));
        account.setAvailablePoints(account.getAvailablePoints().add(request.getPoints()));

        // Save the updated account
        LoyaltyAccount updatedAccount = loyaltyAccountRepository.save(account);

        // Record the transaction
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(request.getUserId());
        transaction.setAmount(request.getPoints());
        transaction.setType(TransactionType.EARN);
        transaction.setReferenceId(request.getReferenceId());
        transaction.setDescription(request.getReason());
        transaction.setEventId(request.getReferenceId()); // Using referenceId as eventId for idempotency
        
        pointsTransactionRepository.save(transaction);

        // Publish PointsEarnedEvent
        PointsEarnedEvent event = new PointsEarnedEvent(
            UUID.randomUUID().toString(),
            request.getUserId(),
            request.getReferenceId(),
            request.getPoints(),
            request.getReason()
        );
        
        outboxService.saveEvent("Loyalty", request.getUserId(), "PointsEarned", event);

        log.info("Points earned: {} for user: {}, new balance: {}", 
                 request.getPoints(), request.getUserId(), updatedAccount.getAvailablePoints());

        return mapToResponse(updatedAccount);
    }

    @Transactional
    public LoyaltyAccountResponse redeemPoints(RedeemPointsRequest request) {
        // Check if transaction already exists (idempotency)
        if (pointsTransactionRepository.existsByEventId(request.getReferenceId())) {
            log.warn("Points already redeemed for reference ID: {}", request.getReferenceId());
            // Return current account state
            return getLoyaltyAccount(request.getUserId());
        }

        // Get loyalty account
        LoyaltyAccount account = loyaltyAccountRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found for user: " + request.getUserId()));

        // Check if sufficient points are available
        if (account.getAvailablePoints().compareTo(request.getPoints()) < 0) {
            throw new IllegalArgumentException("Insufficient points. Available: " + account.getAvailablePoints() + 
                                             ", Requested: " + request.getPoints());
        }

        // Update account balances
        account.setAvailablePoints(account.getAvailablePoints().subtract(request.getPoints()));
        account.setSpentPoints(account.getSpentPoints().add(request.getPoints()));

        // Save the updated account
        LoyaltyAccount updatedAccount = loyaltyAccountRepository.save(account);

        // Record the transaction
        PointsTransaction transaction = new PointsTransaction();
        transaction.setUserId(request.getUserId());
        transaction.setAmount(request.getPoints());
        transaction.setType(TransactionType.REDEEM);
        transaction.setReferenceId(request.getReferenceId());
        transaction.setDescription(request.getReason());
        transaction.setEventId(request.getReferenceId()); // Using referenceId as eventId for idempotency
        
        pointsTransactionRepository.save(transaction);

        // Publish PointsRedeemedEvent
        PointsRedeemedEvent event = new PointsRedeemedEvent(
            UUID.randomUUID().toString(),
            request.getUserId(),
            request.getReferenceId(),
            request.getPoints(),
            request.getReason()
        );
        
        outboxService.saveEvent("Loyalty", request.getUserId(), "PointsRedeemed", event);

        log.info("Points redeemed: {} for user: {}, remaining balance: {}", 
                 request.getPoints(), request.getUserId(), updatedAccount.getAvailablePoints());

        return mapToResponse(updatedAccount);
    }

    public LoyaltyAccountResponse getLoyaltyAccount(String userId) {
        LoyaltyAccount account = loyaltyAccountRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found for user: " + userId));
        
        return mapToResponse(account);
    }

    private LoyaltyAccount createNewLoyaltyAccount(String userId) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setUserId(userId);
        account.setTotalPoints(BigDecimal.ZERO);
        account.setAvailablePoints(BigDecimal.ZERO);
        account.setSpentPoints(BigDecimal.ZERO);
        
        return loyaltyAccountRepository.save(account);
    }

    private LoyaltyAccountResponse mapToResponse(LoyaltyAccount account) {
        LoyaltyAccountResponse response = new LoyaltyAccountResponse();
        response.setUserId(account.getUserId());
        response.setTotalPoints(account.getTotalPoints());
        response.setAvailablePoints(account.getAvailablePoints());
        response.setSpentPoints(account.getSpentPoints());
        return response;
    }
}