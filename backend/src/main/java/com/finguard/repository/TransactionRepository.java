package com.finguard.repository;
import com.finguard.entity.*;
import org.springframework.data.jpa.repository.*;
import java.time.Instant;
import java.util.*;
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
	List<Transaction> findTop20ByUserIdOrderByCreatedAtDesc(UUID userId);
	long countByUserIdAndCreatedAtAfter(UUID userId, Instant after);
	Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
	List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
	List<Transaction> findByStatusInOrderByCreatedAtDesc(Collection<TransactionStatus> statuses);
	long countByStatus(TransactionStatus status);
	long countByUserIdAndStatus(UUID userId, TransactionStatus status);
}
