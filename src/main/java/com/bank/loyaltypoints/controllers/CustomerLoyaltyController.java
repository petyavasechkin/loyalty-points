package com.bank.loyaltypoints.controllers;

import com.bank.loyaltypoints.model.LoyaltyPoints;
import com.bank.loyaltypoints.model.LoyaltyPointsHistory;
import com.bank.loyaltypoints.model.LoyaltyUsage;
import com.bank.loyaltypoints.services.LoyaltyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("loyal-points")
public class CustomerLoyaltyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerLoyaltyController.class);
    private static final String HISTORY_MSG = "Fetching accrued and spent points history for customer with Id: {}";
    private static final String LOYAL_POINTS_MSG = "Fetching available and pending points for customer with Id: {}";
    private static final String USE_POINTS_MSG = "Trying to spent euro amount:{} for customer with Id: {}";

    private final LoyaltyService loyaltyService;

    public CustomerLoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping(value = "/{id}/history", produces = "application/json")
    public ResponseEntity<LoyaltyPointsHistory> getLoyaltyHistory(@PathVariable Long id) {
        LOGGER.debug(HISTORY_MSG, id);
        LoyaltyPointsHistory history = loyaltyService.getHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<LoyaltyPoints> getLoyalty(@PathVariable Long id) {
        LOGGER.debug(LOYAL_POINTS_MSG, id);
        LoyaltyPoints loyalty = loyaltyService.getLoyalty(id);
        return ResponseEntity.ok(loyalty);
    }

    @PutMapping(value = "/{id}/points/{amount}", produces = "application/json")
    public ResponseEntity<LoyaltyUsage> useAvailableLoyalty(@PathVariable Long id,
                                                             @PathVariable String amount) {
        LOGGER.debug(USE_POINTS_MSG, amount, id);
        LoyaltyUsage spent = loyaltyService.spendLoyalty(id, amount);
        return ResponseEntity.ok(spent);
    }

}
