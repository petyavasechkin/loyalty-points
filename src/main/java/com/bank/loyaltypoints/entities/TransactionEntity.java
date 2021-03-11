package com.bank.loyaltypoints.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Transaction")
public class TransactionEntity {

    @Id
    @Column(name = "Id")
    private Long Id;

    @Column(name = "CustomerId")
    private Long customerId;

    @Column(name = "Amount")
    private int amount;

    @Column(name = "DateTime")
    private LocalDateTime dateTime;
}
