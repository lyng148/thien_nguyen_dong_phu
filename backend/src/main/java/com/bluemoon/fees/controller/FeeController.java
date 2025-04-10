package com.bluemoon.fees.controller;

import com.bluemoon.fees.entity.Fee;
import com.bluemoon.fees.service.FeeService;
import com.bluemoon.fees.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees(
            @RequestParam(required = false, defaultValue = "false") boolean showAll) {
        if (showAll) {
            return ResponseEntity.ok(feeService.findAll());
        } else {
            return ResponseEntity.ok(feeService.findAllActive());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fee> getFeeById(@PathVariable Long id) {
        return ResponseEntity.ok(feeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Fee>> getFeesByType(@PathVariable String type) {
        return ResponseEntity.ok(feeService.findByType(type));
    }

    @GetMapping("/due-date-range")
    public ResponseEntity<List<Fee>> getFeesByDueDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(feeService.findByDueDateRange(startDate, endDate));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Fee>> getOverdueFees() {
        return ResponseEntity.ok(feeService.findOverdueFees());
    }

    @PostMapping
    public ResponseEntity<Fee> createFee(@RequestBody Fee fee) {
        return ResponseEntity.ok(feeService.createFee(fee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Fee> updateFee(@PathVariable Long id, @RequestBody Fee fee) {
        return ResponseEntity.ok(feeService.updateFee(id, fee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFee(@PathVariable Long id) {
        feeService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateFee(@PathVariable Long id) {
        feeService.activateFee(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleFeeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusMap) {
        boolean active = statusMap.getOrDefault("active", false);
        if (active) {
            feeService.activateFee(id);
        } else {
            feeService.deactivateFee(id);
        }
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getFeeStatistics(@PathVariable Long id) {
        // Ensure the fee exists
        Fee fee = feeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
        
        // Get payment statistics
        double totalCollected = paymentService.calculateTotalPaymentsByFee(id);
        long totalPayments = paymentService.findByFee(id).size();
        
        Map<String, Object> statistics = Map.of(
            "totalPayments", totalPayments,
            "totalCollected", totalCollected,
            "feeAmount", fee.getAmount(),
            "feeName", fee.getName()
        );
        
        return ResponseEntity.ok(statistics);
    }
} 