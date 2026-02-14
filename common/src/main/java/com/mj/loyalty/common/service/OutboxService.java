package com.mj.loyalty.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.loyalty.common.entity.Outbox;
import com.mj.loyalty.common.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object eventPayload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(eventPayload);
            
            Outbox outbox = new Outbox();
            outbox.setAggregateType(aggregateType);
            outbox.setAggregateId(aggregateId);
            outbox.setEventType(eventType);
            outbox.setPayload(payloadJson);
            
            Outbox savedOutbox = outboxRepository.save(outbox);
            
            // Register a synchronization to publish the event after the transaction commits
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            // Publish to Kafka after the transaction commits with delivery guarantee
                            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(eventType, savedOutbox.getId().toString(), payloadJson);
                            
                            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                                @Override
                                public void onSuccess(SendResult<String, Object> result) {
                                    // Mark as processed after successful delivery for at-least-once semantics
                                    markAsProcessed(savedOutbox.getId());
                                    log.info("Successfully published event {} to topic {}", savedOutbox.getId(), eventType);
                                }

                                @Override
                                public void onFailure(Throwable ex) {
                                    log.error("Failed to publish event {} to topic {}", savedOutbox.getId(), eventType, ex);
                                    // For at-least-once, we leave the event unprocessed so it can be retried later
                                    // The scheduled processor will attempt to resend it
                                }
                            });
                        } catch (Exception e) {
                            log.error("Exception during event publishing for event {}: {}", savedOutbox.getId(), e.getMessage());
                        }
                    }
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload", e);
            throw new RuntimeException("Failed to save event to outbox", e);
        }
    }

    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        var pendingEvents = outboxRepository.findUnprocessedEvents();
        
        for (Outbox event : pendingEvents) {
            try {
                // Publish to Kafka with delivery guarantee
                ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(event.getEventType(), event.getId().toString(), event.getPayload());
                
                future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        // Mark as processed after successful delivery
                        markAsProcessed(event.getId());
                        log.info("Successfully published pending event {} to topic {}", event.getId(), event.getEventType());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Failed to publish pending event {}", event.getId(), ex);
                        // For at-least-once, we leave the event unprocessed so it can be retried later
                    }
                });
            } catch (Exception e) {
                log.error("Exception during pending event publishing for event {}: {}", event.getId(), e.getMessage());
            }
        }
    }
    
    @Transactional
    public void markAsProcessed(Long eventId) {
        Outbox outbox = outboxRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + eventId));
        
        outbox.setProcessed(true);
        outbox.setProcessedAt(java.time.LocalDateTime.now());
        outboxRepository.save(outbox);
    }
}