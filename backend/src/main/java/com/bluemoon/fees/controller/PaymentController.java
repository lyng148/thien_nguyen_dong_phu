package com.bluemoon.fees.controller;

import com.bluemoon.fees.entity.Payment;
import com.bluemoon.fees.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bluemoon.fees.dto.PaymentDTO;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return ResponseEntity.ok(new PaymentDTO(payment));
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByHousehold(@PathVariable Long householdId) {
        List<Payment> payments = paymentService.findByHousehold(householdId);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/fee/{feeId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByFee(@PathVariable Long feeId) {
        List<Payment> payments = paymentService.findByFee(feeId);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Payment> payments = paymentService.findByDateRange(startDate, LocalDate.from(endDate));
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/unverified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentDTO>> getUnverifiedPayments() {
        List<Payment> payments = paymentService.findUnverifiedPayments();
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }

    @GetMapping("/household/{householdId}/fee/{feeId}")
    public ResponseEntity<PaymentDTO> getPaymentByHouseholdAndFee(
            @PathVariable Long householdId,
            @PathVariable Long feeId) {
        Payment payment = paymentService.findByHouseholdAndFee(householdId, feeId);
        return ResponseEntity.ok(new PaymentDTO(payment));
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody Payment payment) {
        log.info("Received payment creation request with payment: {}", payment);
        log.info("Household ID in request: {}", payment.getHousehold() != null ? payment.getHousehold().getId() : "null");
        log.info("Fee ID in request: {}", payment.getFee() != null ? payment.getFee().getId() : "null");
        
        if (payment.getHousehold() != null) {
            log.info("Household in request: ownerName={}", payment.getHousehold().getOwnerName());
            log.info("Household in request: address={}", payment.getHousehold().getAddress());
        }
        
        if (payment.getFee() != null) {
            log.info("Fee in request: name={}", payment.getFee().getName());
            log.info("Fee in request: amount={}", payment.getFee().getAmount());
        }
        
        Payment createdPayment = paymentService.createPayment(payment);
        log.info("Created payment with ID: {}", createdPayment.getId());
        
        // Fetch the created payment with household and fee data to ensure they're fully loaded
        Payment fullPayment = paymentService.findById(createdPayment.getId())
                .orElseThrow(() -> new RuntimeException("Payment was created but could not be retrieved"));
        
        // Create DTO from the full payment
        PaymentDTO paymentDTO = new PaymentDTO(fullPayment);
        
        log.info("Converted to DTO with household ID: {}, name: {}", 
                 paymentDTO.getHouseholdId(), paymentDTO.getHouseholdOwnerName());
        log.info("Converted to DTO with fee ID: {}, name: {}", 
                 paymentDTO.getFeeId(), paymentDTO.getFeeName());
        
        return ResponseEntity.ok(paymentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        Payment updatedPayment = paymentService.updatePayment(id, payment);
        // Fetch the updated payment with household and fee data to ensure they're fully loaded
        Payment fullPayment = paymentService.findById(updatedPayment.getId())
                .orElseThrow(() -> new RuntimeException("Payment was updated but could not be retrieved"));
        return ResponseEntity.ok(new PaymentDTO(fullPayment));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyPayment(@PathVariable Long id) {
        paymentService.verifyPayment(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unverify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unverifyPayment(@PathVariable Long id) {
        paymentService.unverifyPayment(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        paymentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics/household/{householdId}/total")
    public ResponseEntity<Double> getTotalPaymentsByHousehold(@PathVariable Long householdId) {
        return ResponseEntity.ok(paymentService.calculateTotalPaymentsByHousehold(householdId));
    }

    @GetMapping("/statistics/fee/{feeId}/total")
    public ResponseEntity<Double> getTotalPaymentsByFee(@PathVariable Long feeId) {
        return ResponseEntity.ok(paymentService.calculateTotalPaymentsByFee(feeId));
    }

    @GetMapping("/statistics/date-range/total")
    public ResponseEntity<Double> getTotalPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.calculateTotalPaymentsByDateRange(startDate, LocalDate.from(endDate)));
    }
} 