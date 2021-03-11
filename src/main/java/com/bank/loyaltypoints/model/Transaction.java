package com.bank.loyaltypoints.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Transaction {

    private Long id;
    private Long customerId;
    private int amount;
    private LocalDateTime dateTime;
}
