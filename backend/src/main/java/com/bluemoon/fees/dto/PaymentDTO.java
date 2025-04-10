package com.bluemoon.fees.dto;

import java.time.LocalDate;

import com.bluemoon.fees.entity.Fee;
import com.bluemoon.fees.entity.Household;
import com.bluemoon.fees.entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    
    // Household details
    private Long householdId;
    private String householdOwnerName;
    private String householdAddress;
    
    // Fee details
    private Long feeId;
    private String feeName;
    private Double feeAmount;
    
    // Payment details
    private LocalDate paymentDate;
    private Double amount;
    private Double amountPaid;
    private boolean verified;
    private String notes;
    
    // Constructor from Payment entity
    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        
        if (payment.getHousehold() != null) {
            Household household = payment.getHousehold();
            this.householdId = household.getId();
            this.householdOwnerName = household.getOwnerName();
            this.householdAddress = household.getAddress();
        }
        
        if (payment.getFee() != null) {
            Fee fee = payment.getFee();
            this.feeId = fee.getId();
            this.feeName = fee.getName();
            this.feeAmount = fee.getAmount();
        }
        
        this.paymentDate = payment.getPaymentDate();
        this.amount = payment.getAmount();
        this.amountPaid = payment.getAmountPaid();
        this.verified = payment.isVerified();
        this.notes = payment.getNotes();
    }
    
    // Convert back to Payment entity
    public Payment toEntity() {
        Payment payment = new Payment();
        payment.setId(this.id);
        
        if (this.householdId != null) {
            Household household = new Household();
            household.setId(this.householdId);
            household.setOwnerName(this.householdOwnerName);
            household.setAddress(this.householdAddress);
            payment.setHousehold(household);
        }
        
        if (this.feeId != null) {
            Fee fee = new Fee();
            fee.setId(this.feeId);
            fee.setName(this.feeName);
            fee.setAmount(this.feeAmount);
            payment.setFee(fee);
        }
        
        payment.setPaymentDate(this.paymentDate);
        payment.setAmount(this.amount);
        payment.setAmountPaid(this.amountPaid);
        payment.setVerified(this.verified);
        payment.setNotes(this.notes);
        
        return payment;
    }
} 