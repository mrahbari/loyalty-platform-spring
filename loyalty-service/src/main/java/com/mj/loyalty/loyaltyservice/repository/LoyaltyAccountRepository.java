package com.mj.loyalty.loyaltyservice.repository;

import com.mj.loyalty.loyaltyservice.entity.LoyaltyAccount;
import com.mj.loyalty.loyaltyservice.entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByUserId(String userId);
}