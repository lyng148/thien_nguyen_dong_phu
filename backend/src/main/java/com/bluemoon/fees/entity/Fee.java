package com.bluemoon.fees.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fees")
@ToString(exclude = {"payments"})
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id", 
  scope = Fee.class
)
public class Fee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type; // MANDATORY or VOLUNTARY
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    private String description;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Payment> payments;
} 