package com.finguard.repository;
import com.finguard.entity.RiskEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface RiskEvaluationRepository extends JpaRepository<RiskEvaluation, UUID> {
	Optional<RiskEvaluation> findByTransactionId(UUID transactionId);
	List<RiskEvaluation> findTop20ByTransactionUserIdOrderByEvaluatedAtDesc(UUID userId);
}
