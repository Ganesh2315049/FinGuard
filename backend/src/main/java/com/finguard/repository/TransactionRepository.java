package com.finguard.repository;
import com.finguard.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface TransactionRepository extends JpaRepository<Transaction, UUID> { List<Transaction> findTop20ByUserIdOrderByCreatedAtDesc(UUID userId); long countByStatus(TransactionStatus status); }
