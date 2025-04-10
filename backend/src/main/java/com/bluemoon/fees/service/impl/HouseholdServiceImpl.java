package com.bluemoon.fees.service.impl;

import com.bluemoon.fees.entity.Household;
import com.bluemoon.fees.repository.HouseholdRepository;
import com.bluemoon.fees.service.HouseholdService;
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
        household.setActive(true);
        return save(household);
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