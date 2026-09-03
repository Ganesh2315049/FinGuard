package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Getter @Setter @NoArgsConstructor
public class FraudRule {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false, unique=true) private String name;
    private String description;
    @Column(nullable=false) private String ruleType;
    @Column(nullable=false) private int score;
    @Column(nullable=false) private boolean enabled=true;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
    private Instant updatedAt=Instant.now();
    public String getRuleType(){return ruleType;} public int getScore(){return score;}
}
