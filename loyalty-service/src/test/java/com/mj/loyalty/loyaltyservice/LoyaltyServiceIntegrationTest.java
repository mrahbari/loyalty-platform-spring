package com.mj.loyalty.loyaltyservice;

import com.mj.loyalty.loyaltyservice.dto.EarnPointsRequest;
import com.mj.loyalty.loyaltyservice.dto.LoyaltyAccountResponse;
import com.mj.loyalty.loyaltyservice.service.LoyaltyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class LoyaltyServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private LoyaltyService loyaltyService;

    @Test
    void testEarnPoints() {
        // Given
        EarnPointsRequest request = new EarnPointsRequest();
        request.setUserId("user-123");
        request.setPoints(BigDecimal.valueOf(100.00));
        request.setReferenceId("order-456");
        request.setReason("Purchase");

        // When
        LoyaltyAccountResponse response = loyaltyService.earnPoints(request);

        // Then
        assertEquals("user-123", response.getUserId());
        assertEquals(BigDecimal.valueOf(100.00), response.getTotalPoints());
        assertEquals(BigDecimal.valueOf(100.00), response.getAvailablePoints());
        assertEquals(BigDecimal.ZERO, response.getSpentPoints());
    }
}