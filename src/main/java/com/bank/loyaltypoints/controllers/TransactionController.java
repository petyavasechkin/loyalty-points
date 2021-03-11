package com.bank.loyaltypoints.controllers;

import com.bank.loyaltypoints.model.Transaction;
import com.bank.loyaltypoints.services.LoyaltyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    private static final String NEW_TRANSACT_MSG = "Received transaction for customer Id:{} with amount:{}, timeStamp:{}";

    private final LoyaltyService loyaltyService;

    public TransactionController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Transaction transaction) {
        LOGGER.debug(NEW_TRANSACT_MSG, transaction.getCustomerId(), transaction.getAmount(), transaction.getDateTime());
        loyaltyService.processTransaction(transaction);
        return ResponseEntity.accepted().build();
    }
}
