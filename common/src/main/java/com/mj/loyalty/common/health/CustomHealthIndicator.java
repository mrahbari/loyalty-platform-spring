package com.mj.loyalty.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Perform custom health check logic here
        // For now, returning UP status
        return Health.up()
                .withDetail("custom", "Custom health check is running")
                .build();
    }
}