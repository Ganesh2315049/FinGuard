package com.finguard.repository;
import com.finguard.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, UUID> { Optional<IdempotencyRecord> findByIdempotencyKeyAndUserId(String key, UUID userId); }
