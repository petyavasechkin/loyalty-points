package com.bank.loyaltypoints.repositories;

import com.bank.loyaltypoints.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> getAllByCustomerIdAndDateTimeAfter(Long id, LocalDateTime dateTime);
}
