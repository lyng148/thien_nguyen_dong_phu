package com.bluemoon.fees.service.impl;

import com.bluemoon.fees.entity.Fee;
import com.bluemoon.fees.entity.Notification;
import com.bluemoon.fees.entity.User;
import com.bluemoon.fees.repository.FeeRepository;
import com.bluemoon.fees.service.FeeService;
import com.bluemoon.fees.service.NotificationService;
import com.bluemoon.fees.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Override
    public Fee save(Fee entity) {
        return feeRepository.save(entity);
    }

    @Override
    public List<Fee> saveAll(List<Fee> entities) {
        return feeRepository.saveAll(entities);
    }

    @Override
    public Optional<Fee> findById(Long id) {
        return feeRepository.findById(id);
    }

    @Override
    public List<Fee> findAll() {
        return feeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        feeRepository.deleteById(id);
    }

    @Override
    public void delete(Fee entity) {
        feeRepository.delete(entity);
    }

    @Override
    public boolean existsById(Long id) {
        return feeRepository.existsById(id);
    }

    @Override
    public List<Fee> findAllActive() {
        return feeRepository.findByActiveTrue();
    }

    @Override
    public Fee findActiveById(Long id) {
        return feeRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Active fee not found with id: " + id));
    }

    @Override
    public List<Fee> findByType(String type) {
        return feeRepository.findByTypeAndActiveTrue(type);
    }

    @Override
    public List<Fee> findByDueDateRange(LocalDate startDate, LocalDate endDate) {
        return feeRepository.findByDueDateBetweenAndActiveTrue(startDate, endDate);
    }

    @Override
    public List<Fee> findOverdueFees() {
        return feeRepository.findByDueDateBeforeAndActiveTrue(LocalDate.now());
    }

    @Override
    public Fee createFee(Fee fee) {
        // Không tự động đặt fee.setActive(true) nữa
        // Để giữ nguyên giá trị active từ frontend gửi lên
        Fee savedFee = feeRepository.save(fee);
        
        // Create notification for admin
        User admin = userService.findAdminUser();
        
        // Đảm bảo thông tin fee an toàn
        String feeName = fee.getName() != null ? fee.getName() : "Unknown";
        String feeAmount = fee.getAmount() != null ? fee.getAmount().toString() : "N/A";
        
        notificationService.createNotification(
            "New Fee Created",
            String.format("A new fee '%s' has been created with amount %s", feeName, feeAmount),
            Notification.EntityType.FEE,
            savedFee.getId(),
            admin
        );
        
        return savedFee;
    }

    @Override
    public Fee updateFee(Long id, Fee fee) {
        Fee existingFee = findActiveById(id);
        
        existingFee.setName(fee.getName());
        existingFee.setType(fee.getType());
        existingFee.setAmount(fee.getAmount());
        existingFee.setDueDate(fee.getDueDate());
        existingFee.setDescription(fee.getDescription());
        existingFee.setActive(fee.isActive());
        
        return save(existingFee);
    }

    @Override
    public void deactivateFee(Long id) {
        Fee fee = findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
        fee.setActive(false);
        save(fee);
    }

    @Override
    public void activateFee(Long id) {
        Fee fee = findById(id)
                .orElseThrow(() -> new RuntimeException("Fee not found with id: " + id));
        fee.setActive(true);
        save(fee);
    }
} 