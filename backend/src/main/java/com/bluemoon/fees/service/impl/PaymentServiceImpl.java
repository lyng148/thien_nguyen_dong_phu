package com.bluemoon.fees.service.impl;

import com.bluemoon.fees.entity.Payment;
import com.bluemoon.fees.entity.Notification;
import com.bluemoon.fees.entity.User;
import com.bluemoon.fees.repository.PaymentRepository;
import com.bluemoon.fees.service.PaymentService;
import com.bluemoon.fees.service.NotificationService;
import com.bluemoon.fees.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Override
    public Payment save(Payment entity) {
        return paymentRepository.save(entity);
    }

    @Override
    public List<Payment> saveAll(List<Payment> entities) {
        return paymentRepository.saveAll(entities);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findByIdWithHouseholdAndFee(id);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAllWithHouseholdAndFee();
    }

    @Override
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public void delete(Payment entity) {
        paymentRepository.delete(entity);
    }

    @Override
    public boolean existsById(Long id) {
        return paymentRepository.existsById(id);
    }

    @Override
    public List<Payment> findByHousehold(Long householdId) {
        return paymentRepository.findByHouseholdId(householdId);
    }

    @Override
    public List<Payment> findByFee(Long feeId) {
        return paymentRepository.findByFeeId(feeId);
    }

    @Override
    public List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    @Override
    public List<Payment> findUnverifiedPayments() {
        return paymentRepository.findByVerifiedFalse();
    }

    @Override
    public Payment findByHouseholdAndFee(Long householdId, Long feeId) {
        return paymentRepository.findByHouseholdIdAndFeeId(householdId, feeId)
                .orElseThrow(() -> new RuntimeException("Payment not found for household: " + householdId + " and fee: " + feeId));
    }

    @Override
    public List<Payment> findByHouseholdAndDateRange(Long householdId, LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByHouseholdIdAndPaymentDateBetween(householdId, startDate, endDate);
    }

    @Override
    public Payment createPayment(Payment payment) {
        // Set default values
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        // Không tự động đặt payment.setVerified(false) nữa
        // Để giữ nguyên giá trị verified từ frontend gửi lên
        
        if (payment.getAmountPaid() == null) {
            payment.setAmountPaid(payment.getAmount());
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Create notification for admin
        User admin = userService.findAdminUser();
        
        // Tạo thông điệp an toàn với xử lý null
        String householdInfo = "unknown";
        if (payment.getHousehold() != null) {
            if (payment.getHousehold().getOwnerName() != null) {
                householdInfo = payment.getHousehold().getOwnerName();
            } else if (payment.getHousehold().getId() != null) {
                householdInfo = "ID: " + payment.getHousehold().getId();
            }
        }
        
        notificationService.createNotification(
            "New Payment Received",
            String.format("A new payment of %s has been received for household %s", 
                payment.getAmount(), 
                householdInfo),
            Notification.EntityType.PAYMENT,
            savedPayment.getId(),
            admin
        );
        
        return savedPayment;
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        Payment existingPayment = findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        existingPayment.setAmount(payment.getAmount());
        existingPayment.setNotes(payment.getNotes());
        
        if (payment.getAmountPaid() != null) {
            existingPayment.setAmountPaid(payment.getAmountPaid());
        }
        
        // If payment has new household, update it
        if (payment.getHousehold() != null && payment.getHousehold().getId() != null) {
            existingPayment.setHousehold(payment.getHousehold());
        }
        
        // If payment has new fee, update it
        if (payment.getFee() != null && payment.getFee().getId() != null) {
            existingPayment.setFee(payment.getFee());
        }
        
        // Update payment date if provided
        if (payment.getPaymentDate() != null) {
            existingPayment.setPaymentDate(payment.getPaymentDate());
        }
        
        // Update verification status if provided
        existingPayment.setVerified(payment.isVerified());
        
        return save(existingPayment);
    }

    @Override
    public void verifyPayment(Long id) {
        Payment payment = findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setVerified(true);
        save(payment);
    }

    @Override
    public void unverifyPayment(Long id) {
        Payment payment = findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setVerified(false);
        save(payment);
    }

    @Override
    public Double calculateTotalPaymentsByHousehold(Long householdId) {
        return findByHousehold(householdId).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public Double calculateTotalPaymentsByFee(Long feeId) {
        return findByFee(feeId).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public Double calculateTotalPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return findByDateRange(startDate, endDate).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }
} 