package com.mj.loyalty.loyaltyservice.repository;

import com.mj.loyalty.loyaltyservice.entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {
    Optional<PointsTransaction> findByEventId(String eventId);
    boolean existsByEventId(String eventId);
}