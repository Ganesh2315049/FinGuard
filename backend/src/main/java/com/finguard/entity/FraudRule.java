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
    public UUID getId(){return id;} public String getName(){return name;} public String getDescription(){return description;} public String getRuleType(){return ruleType;} public int getScore(){return score;} public boolean isEnabled(){return enabled;}
    public void setName(String value){name=value;} public void setDescription(String value){description=value;} public void setRuleType(String value){ruleType=value;} public void setScore(int value){score=value;} public void setEnabled(boolean value){enabled=value;}
}
