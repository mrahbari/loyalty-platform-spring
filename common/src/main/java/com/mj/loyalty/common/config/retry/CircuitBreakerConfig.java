package com.mj.loyalty.common.config.retry;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {
    // Configuration for Resilience4j is handled automatically by Spring Cloud
    // Just need to add the dependency and enable annotations in main classes
}