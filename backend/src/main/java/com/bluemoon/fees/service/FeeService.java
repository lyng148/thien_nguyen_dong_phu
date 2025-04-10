package com.bluemoon.fees.service;

import com.bluemoon.fees.entity.Fee;
import java.time.LocalDate;
import java.util.List;

public interface FeeService extends BaseService<Fee, Long> {
    List<Fee> findAllActive();
    Fee findActiveById(Long id);
    List<Fee> findByType(String type);
    List<Fee> findByDueDateRange(LocalDate startDate, LocalDate endDate);
    List<Fee> findOverdueFees();
    Fee createFee(Fee fee);
    Fee updateFee(Long id, Fee fee);
    void deactivateFee(Long id);
    void activateFee(Long id);
} 