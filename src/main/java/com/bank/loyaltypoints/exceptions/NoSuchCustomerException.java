package com.bank.loyaltypoints.exceptions;

public class NoSuchCustomerException extends RuntimeException {
    public NoSuchCustomerException(String msg) {
        super(msg);
    }
}
