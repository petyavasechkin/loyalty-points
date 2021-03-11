package com.bank.loyaltypoints.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CustomerLoyal")
public class CustomerLoyalEntity {

    @Id
    @Column(name = "CustomerId")
    private Long customerId;

    @Column(name = "LastUpdated")
    private LocalDateTime lastUpdated;

    @Column(name = "LastAccruedDate")
    private LocalDate lastAccruedDate;

    @Column(name = "AvailablePoints")
    private int availablePoints;

    @Column(name = "PendingPoints")
    private int pendingPoints;

    @Column(name = "AllAccruedPoints")
    private int allAccruedPoints;

    @Column(name = "AllSpentPoints")
    private int allSpentPoints;
}
