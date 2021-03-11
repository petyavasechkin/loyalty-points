package com.bank.loyaltypoints.services;

import com.bank.loyaltypoints.model.LoyaltyPoints;
import com.bank.loyaltypoints.model.LoyaltyPointsHistory;
import com.bank.loyaltypoints.model.LoyaltyUsage;
import com.bank.loyaltypoints.model.Transaction;

public interface LoyaltyService {

    void processTransaction(Transaction transaction);
    LoyaltyPointsHistory getHistory(Long customerId);
    LoyaltyPoints getLoyalty(Long customerId);
    LoyaltyUsage spendLoyalty(Long customerId, String amount);
}
