package com.bluemoon.fees.service;

import com.bluemoon.fees.entity.Payment;
import java.time.LocalDate;
import java.util.List;

public interface PaymentService extends BaseService<Payment, Long> {
    List<Payment> findByHousehold(Long householdId);
    List<Payment> findByFee(Long feeId);
    List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Payment> findUnverifiedPayments();
    Payment findByHouseholdAndFee(Long householdId, Long feeId);
    List<Payment> findByHouseholdAndDateRange(Long householdId, LocalDate startDate, LocalDate endDate);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    void verifyPayment(Long id);
    void unverifyPayment(Long id);
    Double calculateTotalPaymentsByHousehold(Long householdId);
    Double calculateTotalPaymentsByFee(Long feeId);
    Double calculateTotalPaymentsByDateRange(LocalDate startDate, LocalDate endDate);
} 