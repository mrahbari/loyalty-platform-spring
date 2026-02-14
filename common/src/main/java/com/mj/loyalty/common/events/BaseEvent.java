package com.mj.loyalty.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String eventType;
}