package com.finguard.repository;
import com.finguard.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {}