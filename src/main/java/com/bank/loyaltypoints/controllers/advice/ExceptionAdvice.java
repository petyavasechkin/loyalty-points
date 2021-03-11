package com.bank.loyaltypoints.controllers.advice;

import com.bank.loyaltypoints.exceptions.NoSuchCustomerException;
import com.bank.loyaltypoints.exceptions.NotEnoughAvailablePointsException;
import com.bank.loyaltypoints.exceptions.TransactionAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);
    private static final String TRANS_ERR_MSG = "Exception occurred during transaction processing: {}";
    private static final String NO_CUSTOMER_MSG = "Exception occurred during fetching points info for customer";
    private static final String NOT_ENOUGH_POINTS_MSG = "Exception occurred during a try spent money due to not enough available points.";

    @ExceptionHandler({TransactionAlreadyExistsException.class})
    public final ResponseEntity<String> handleTransactionDuplicateException(Exception ex) {
        LOGGER.warn(TRANS_ERR_MSG, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({NoSuchCustomerException.class})
    public final ResponseEntity<String> handleNoCustomerException(Exception ex) {
        LOGGER.warn(NO_CUSTOMER_MSG, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({NotEnoughAvailablePointsException.class})
    public final ResponseEntity<String> handleNotEnoughPointsException(Exception ex) {
        LOGGER.warn(NOT_ENOUGH_POINTS_MSG, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }


}
