package com.bluemoon.fees.repository;

import com.bluemoon.fees.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByHouseholdId(Long householdId);
    List<Payment> findByFeeId(Long feeId);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Payment> findByVerifiedFalse();
    Optional<Payment> findByHouseholdIdAndFeeId(Long householdId, Long feeId);
    List<Payment> findByHouseholdIdAndPaymentDateBetween(Long householdId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Payment p JOIN FETCH p.household JOIN FETCH p.fee")
    List<Payment> findAllWithHouseholdAndFee();
    
    @Query("SELECT p FROM Payment p JOIN FETCH p.household JOIN FETCH p.fee WHERE p.id = :id")
    Optional<Payment> findByIdWithHouseholdAndFee(Long id);
} 