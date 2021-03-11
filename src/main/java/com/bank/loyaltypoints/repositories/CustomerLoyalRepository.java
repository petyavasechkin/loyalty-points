package com.bank.loyaltypoints.repositories;

import com.bank.loyaltypoints.entities.CustomerLoyalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLoyalRepository extends JpaRepository<CustomerLoyalEntity, Long> {
}
