package com.bank.loyaltypoints.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CalcPointsContext {
    private Transaction transaction;
    private int firstBound;
    private int secondBound;
    private int firstFactor;
    private int secondFactor;
    private int thirdFactor;
}
