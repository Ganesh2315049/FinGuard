package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Getter @Setter @NoArgsConstructor
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) private User user;
    @Column(nullable=false) private String action;
    private String entityType;
    private UUID entityId;
    private String description;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
}
