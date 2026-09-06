package com.finguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="users", indexes=@Index(name="idx_users_email", columnList="email", unique=true))
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private UserRole role=UserRole.CUSTOMER;
    @Column(nullable=false) private boolean enabled=true;
    @Column(nullable=false, updatable=false) private Instant createdAt=Instant.now();
    private Instant updatedAt=Instant.now();
    public UUID getId(){return id;} public String getName(){return name;} public Instant getCreatedAt(){return createdAt;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;} public UserRole getRole(){return role;} public void setRole(UserRole v){role=v;} public boolean isEnabled(){return enabled;}
}
