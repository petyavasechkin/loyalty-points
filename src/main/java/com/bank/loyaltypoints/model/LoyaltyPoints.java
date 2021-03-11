package com.bank.loyaltypoints.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoyaltyPoints {
     private int available;
     private int pending;
}
