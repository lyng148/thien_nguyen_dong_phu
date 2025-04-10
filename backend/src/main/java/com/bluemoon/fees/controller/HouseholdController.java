package com.bluemoon.fees.controller;

import com.bluemoon.fees.entity.Household;
import com.bluemoon.fees.entity.Payment;
import com.bluemoon.fees.service.HouseholdService;
import com.bluemoon.fees.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/households")
@RequiredArgsConstructor
@Slf4j
public class HouseholdController {

    private final HouseholdService householdService;
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Household>> getAllHouseholds(
            @RequestParam(required = false, defaultValue = "false") boolean showAll) {
        List<Household> households;
        if (showAll) {
            log.info("Getting all households (including inactive)");
            households = householdService.findAll();
            log.info("Found {} households", households.size());
        } else {
            log.info("Getting all active households");
            households = householdService.findAllActive();
            log.info("Found {} active households", households.size());
        }
        return ResponseEntity.ok(households);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Household> getHouseholdById(@PathVariable Long id) {
        log.info("Getting household with id: {}", id);
        Household household = householdService.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        log.info("Found household: {}", household);
        return ResponseEntity.ok(household);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Household>> searchHouseholds(
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false, defaultValue = "false") boolean showAll) {
        log.info("Searching households with ownerName: {}, address: {}, showAll: {}", ownerName, address, showAll);
        List<Household> households;
        
        if (ownerName != null && !ownerName.isEmpty()) {
            households = householdService.searchByOwnerName(ownerName);
            log.info("Found {} households by owner name", households.size());
        } else if (address != null && !address.isEmpty()) {
            households = householdService.searchByAddress(address);
            log.info("Found {} households by address", households.size());
        } else {
            if (showAll) {
                households = householdService.findAll();
                log.info("No search parameters, returning all {} households", households.size());
            } else {
                households = householdService.findAllActive();
                log.info("No search parameters, returning all {} active households", households.size());
            }
        }
        
        return ResponseEntity.ok(households);
    }

    @PostMapping
    public ResponseEntity<Household> createHousehold(@RequestBody Household household) {
        log.info("Creating new household: {}", household);
        Household created = householdService.createHousehold(household);
        log.info("Created household with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Household> updateHousehold(@PathVariable Long id, @RequestBody Household household) {
        log.info("Updating household with id: {}", id);
        Household updated = householdService.updateHousehold(id, household);
        log.info("Updated household: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateHousehold(@PathVariable Long id) {
        log.info("Handling delete request for household with id: {}", id);
        
        // Check if the household is already inactive
        Household household = householdService.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        
        if (household.isActive()) {
            // If active, just deactivate it
            log.info("Deactivating active household with id: {}", id);
            householdService.deactivateHousehold(id);
            log.info("Household deactivated successfully");
        } else {
            // If already inactive, permanently delete it
            log.info("Permanently deleting inactive household with id: {}", id);
            householdService.deleteById(id);
            log.info("Household permanently deleted");
        }
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateHousehold(@PathVariable Long id) {
        log.info("Activating household with id: {}", id);
        householdService.activateHousehold(id);
        log.info("Household activated successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<Payment>> getHouseholdPayments(@PathVariable Long id) {
        log.info("Getting payments for household with id: {}", id);
        List<Payment> payments = paymentService.findByHousehold(id);
        log.info("Found {} payments", payments.size());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getHouseholdStatistics(@PathVariable Long id) {
        log.info("Getting statistics for household with id: {}", id);
        
        // Ensure the household exists
        householdService.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        
        // Get payment statistics
        double totalPaid = paymentService.calculateTotalPaymentsByHousehold(id);
        List<Payment> payments = paymentService.findByHousehold(id);
        long verifiedCount = payments.stream().filter(Payment::isVerified).count();
        double verifiedPercentage = payments.isEmpty() ? 0 : (verifiedCount * 100.0) / payments.size();
        
        Map<String, Object> statistics = Map.of(
            "totalPayments", payments.size(),
            "totalPaid", totalPaid,
            "verifiedCount", verifiedCount,
            "verifiedPercentage", verifiedPercentage
        );
        
        log.info("Statistics for household {}: {}", id, statistics);
        return ResponseEntity.ok(statistics);
    }
} 