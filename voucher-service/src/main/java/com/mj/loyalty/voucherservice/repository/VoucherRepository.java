package com.mj.loyalty.voucherservice.repository;

import com.mj.loyalty.voucherservice.entity.Voucher;
import com.mj.loyalty.voucherservice.enums.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByVoucherId(String voucherId);
    Optional<Voucher> findByVoucherCode(String voucherCode);
    List<Voucher> findByUserId(String userId);
    List<Voucher> findByUserIdAndStatus(String userId, VoucherStatus status);
    
    @Modifying
    @Query("UPDATE Voucher v SET v.status = ?2 WHERE v.voucherId = ?1 AND v.status = 'ACTIVE'")
    int updateVoucherStatus(String voucherId, VoucherStatus status);
    
    @Query("SELECT v FROM Voucher v WHERE v.expirationDate < ?1 AND v.status = 'ACTIVE'")
    List<Voucher> findExpiredVouchers(LocalDateTime dateTime);
}