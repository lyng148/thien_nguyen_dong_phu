package com.bluemoon.fees.service.impl;

import com.bluemoon.fees.entity.Household;
import com.bluemoon.fees.entity.Notification;
import com.bluemoon.fees.entity.User;
import com.bluemoon.fees.repository.HouseholdRepository;
import com.bluemoon.fees.service.HouseholdService;
import com.bluemoon.fees.service.NotificationService;
import com.bluemoon.fees.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdRepository householdRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public Household save(Household entity) {
        return householdRepository.save(entity);
    }

    @Override
    public List<Household> saveAll(List<Household> entities) {
        return householdRepository.saveAll(entities);
    }

    @Override
    public Optional<Household> findById(Long id) {
        return householdRepository.findById(id);
    }

    @Override
    public List<Household> findAll() {
        return householdRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        householdRepository.deleteById(id);
    }

    @Override
    public void delete(Household entity) {
        householdRepository.delete(entity);
    }

    @Override
    public boolean existsById(Long id) {
        return householdRepository.existsById(id);
    }

    @Override
    public List<Household> findAllActive() {
        return householdRepository.findByActiveTrue();
    }

    @Override
    public Household findActiveById(Long id) {
        return householdRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Active household not found with id: " + id));
    }

    @Override
    public List<Household> searchByOwnerName(String ownerName) {
        return householdRepository.findByOwnerNameContainingIgnoreCase(ownerName);
    }

    @Override
    public List<Household> searchByAddress(String address) {
        return householdRepository.findByAddressContainingIgnoreCase(address);
    }

    @Override
    public Household createHousehold(Household household) {
        // Không tự động đặt household.setActive(true) nữa
        // Để giữ nguyên giá trị active từ frontend gửi lên
        Household savedHousehold = householdRepository.save(household);
        
        // Create notification for admin
        User admin = userService.findAdminUser();
        
        // Đảm bảo thông tin household an toàn
        String householdName = household.getOwnerName() != null ? 
            household.getOwnerName() : "household #" + savedHousehold.getId();
        
        notificationService.createNotification(
            "New Household Added",
            String.format("A new household '%s' has been added", householdName),
            Notification.EntityType.HOUSEHOLD,
            savedHousehold.getId(),
            admin
        );
        
        return savedHousehold;
    }

    @Override
    public Household updateHousehold(Long id, Household household) {
        Household existingHousehold = findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        
        existingHousehold.setOwnerName(household.getOwnerName());
        existingHousehold.setAddress(household.getAddress());
        existingHousehold.setNumMembers(household.getNumMembers());
        existingHousehold.setPhoneNumber(household.getPhoneNumber());
        existingHousehold.setEmail(household.getEmail());
        existingHousehold.setActive(household.isActive());
        
        return save(existingHousehold);
    }

    @Override
    public void deactivateHousehold(Long id) {
        Household household = findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        household.setActive(false);
        save(household);
    }

    @Override
    public void activateHousehold(Long id) {
        Household household = findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        household.setActive(true);
        save(household);
    }
} 