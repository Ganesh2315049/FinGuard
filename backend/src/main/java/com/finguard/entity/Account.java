package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Getter @Setter @NoArgsConstructor
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false, unique=true) private String accountNumber;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) private User user;
    @Column(nullable=false, precision=19, scale=4) private BigDecimal balance=BigDecimal.ZERO;
    @Column(nullable=false, length=3) private String currency="INR";
    @Column(nullable=false) private boolean active=true;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
    private Instant updatedAt=Instant.now();
    public UUID getId(){return id;} public void setUser(User v){user=v;} public User getUser(){return user;} public void setAccountNumber(String v){accountNumber=v;} public String getAccountNumber(){return accountNumber;} public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
}
