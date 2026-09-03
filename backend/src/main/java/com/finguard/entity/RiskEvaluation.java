package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Getter @Setter @NoArgsConstructor
public class RiskEvaluation {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @OneToOne(optional=false) private Transaction transaction;
    @Column(nullable=false) private int score;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private RiskDecision decision;
    @Column(nullable=false, length=2000) private String reasons;
    @Column(nullable=false) private Instant evaluatedAt=Instant.now();
    public RiskDecision getDecision(){return decision;} public void setTransaction(Transaction v){transaction=v;} public void setScore(int v){score=v;} public void setDecision(RiskDecision v){decision=v;} public void setReasons(String v){reasons=v;}
}
