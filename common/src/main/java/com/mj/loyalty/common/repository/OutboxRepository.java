package com.mj.loyalty.common.repository;

import com.mj.loyalty.common.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    @Query("SELECT o FROM Outbox o WHERE o.processed = false ORDER BY o.createdAt ASC")
    List<Outbox> findUnprocessedEvents();
}