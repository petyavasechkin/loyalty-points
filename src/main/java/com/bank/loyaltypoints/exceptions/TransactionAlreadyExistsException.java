package com.bank.loyaltypoints.exceptions;

public class TransactionAlreadyExistsException extends RuntimeException {

    public TransactionAlreadyExistsException(String msg) {
        super(msg);
    }
}
