package com.mj.loyalty.voucherservice;

import com.mj.loyalty.voucherservice.dto.CreateVoucherRequest;
import com.mj.loyalty.voucherservice.dto.VoucherResponse;
import com.mj.loyalty.voucherservice.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class VoucherServiceIntegrationTest {

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
    private VoucherService voucherService;

    @Test
    void testCreateVoucher() {
        // Given
        CreateVoucherRequest request = new CreateVoucherRequest();
        request.setUserId("user-123");
        request.setDiscountAmount(BigDecimal.valueOf(50.00));
        request.setExpirationDate(LocalDateTime.now().plusDays(30));
        request.setDescription("Test voucher");

        // When
        VoucherResponse response = voucherService.createVoucher(request);

        // Then
        assertNotNull(response.getVoucherId());
        assertEquals("user-123", response.getUserId());
        assertEquals(BigDecimal.valueOf(50.00), response.getDiscountAmount());
        assertEquals("Test voucher", response.getDescription());
        assertNotNull(response.getVoucherCode());
    }
}