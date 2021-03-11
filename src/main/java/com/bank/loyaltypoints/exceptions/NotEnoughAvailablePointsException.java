package com.bank.loyaltypoints.exceptions;

public class NotEnoughAvailablePointsException extends RuntimeException {
    public NotEnoughAvailablePointsException(String msg) {
        super(msg);
    }
}
