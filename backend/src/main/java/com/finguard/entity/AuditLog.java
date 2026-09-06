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
    public UUID getId(){return id;} public String getAction(){return action;} public String getEntityType(){return entityType;} public UUID getEntityId(){return entityId;} public String getDescription(){return description;} public Instant getCreatedAt(){return createdAt;}
    public void setUser(User value){user=value;} public void setAction(String value){action=value;} public void setEntityType(String value){entityType=value;} public void setEntityId(UUID value){entityId=value;} public void setDescription(String value){description=value;}
}
