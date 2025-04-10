package com.bluemoon.fees.repository;

import com.bluemoon.fees.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByActiveTrue();
    Optional<Fee> findByIdAndActiveTrue(Long id);
    List<Fee> findByTypeAndActiveTrue(String type);
    List<Fee> findByDueDateBetweenAndActiveTrue(LocalDate startDate, LocalDate endDate);
    List<Fee> findByDueDateBeforeAndActiveTrue(LocalDate date);
} 