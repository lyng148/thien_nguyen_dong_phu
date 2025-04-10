package com.bluemoon.fees.repository;

import com.bluemoon.fees.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByActiveTrue();
    Optional<Household> findByIdAndActiveTrue(Long id);
    List<Household> findByOwnerNameContainingIgnoreCase(String ownerName);
    List<Household> findByAddressContainingIgnoreCase(String address);
} 