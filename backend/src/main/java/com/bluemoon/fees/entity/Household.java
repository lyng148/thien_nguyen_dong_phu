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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "households")
@ToString(exclude = {"payments"})
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id",
  scope = Household.class
)
public class Household {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    
    @Column(nullable = false)
    private String address;
    
    @Column(name = "num_members", nullable = false)
    private Integer numMembers;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column
    private String email;
    
    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Payment> payments;
    
    @Column(nullable = false)
    private boolean active = true;
} 