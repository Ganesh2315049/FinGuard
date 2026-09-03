package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="transactions", indexes={@Index(name="idx_tx_created", columnList="createdAt"), @Index(name="idx_tx_status", columnList="status")})
@Getter @Setter @NoArgsConstructor
public class Transaction {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false, unique=true) private String transactionReference;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionType type;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionStatus status;
    @Column(nullable=false, precision=19, scale=4) private BigDecimal amount;
    @ManyToOne(fetch=FetchType.LAZY) private Account sourceAccount;
    @ManyToOne(fetch=FetchType.LAZY) private Account destinationAccount;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) private User user;
    private String description;
    private String location;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
    private Instant updatedAt=Instant.now();
    public UUID getId(){return id;} public BigDecimal getAmount(){return amount;} public Instant getCreatedAt(){return createdAt;} public TransactionType getType(){return type;} public TransactionStatus getStatus(){return status;} public String getTransactionReference(){return transactionReference;} public void setUser(User v){user=v;} public void setSourceAccount(Account v){sourceAccount=v;} public void setDestinationAccount(Account v){destinationAccount=v;} public void setType(TransactionType v){type=v;} public void setAmount(BigDecimal v){amount=v;} public void setStatus(TransactionStatus v){status=v;} public void setTransactionReference(String v){transactionReference=v;}
}
