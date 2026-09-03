package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(uniqueConstraints=@UniqueConstraint(name="uk_idempotency_key_user", columnNames={"idempotencyKey","user_id"}))
@Getter @Setter @NoArgsConstructor
public class IdempotencyRecord {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private String idempotencyKey;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) private User user;
    @OneToOne(optional=false) private Transaction transaction;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
}
