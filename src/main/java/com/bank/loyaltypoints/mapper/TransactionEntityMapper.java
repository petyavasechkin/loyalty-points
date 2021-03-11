package com.bank.loyaltypoints.mapper;

import com.bank.loyaltypoints.entities.TransactionEntity;
import com.bank.loyaltypoints.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionEntityMapper {

    TransactionEntity covertTransactionDtoToEntity(Transaction transaction);
}
