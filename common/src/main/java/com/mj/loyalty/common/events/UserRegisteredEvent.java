package com.mj.loyalty.common.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRegisteredEvent extends BaseEvent {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    
    public UserRegisteredEvent(String eventId, String userId, String email, String firstName, String lastName) {
        super(eventId, java.time.LocalDateTime.now(), "UserRegistered");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}