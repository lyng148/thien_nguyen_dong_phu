package com.bluemoon.fees.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@ToString(exclude = {"household", "fee"})
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id",
  scope = Payment.class
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Household household;
    
    @ManyToOne
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Fee fee;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid = 0.0;
    
    @Column(nullable = false)
    private boolean verified = false;
    
    private String notes;
} 