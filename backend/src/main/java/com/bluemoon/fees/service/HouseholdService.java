package com.bluemoon.fees.service;

import com.bluemoon.fees.entity.Household;
import java.util.List;

public interface HouseholdService extends BaseService<Household, Long> {
    List<Household> findAllActive();
    Household findActiveById(Long id);
    List<Household> searchByOwnerName(String ownerName);
    List<Household> searchByAddress(String address);
    Household createHousehold(Household household);
    Household updateHousehold(Long id, Household household);
    void deactivateHousehold(Long id);
    void activateHousehold(Long id);
} 