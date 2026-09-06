package com.finguard.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.finguard.entity.RiskDecision;
import com.finguard.entity.RiskEvaluation;
import com.finguard.entity.Transaction;
import com.finguard.entity.User;
import com.finguard.repository.FraudRuleRepository;
import com.finguard.repository.RiskEvaluationRepository;
import com.finguard.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RiskEvaluationServiceTest {
    @Mock RiskEvaluationRepository evaluations;
    @Mock FraudRuleRepository rules;
    @Mock TransactionRepository transactions;

    @Test
    void approvesLowRiskTransaction() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setName("Customer");
        user.setEmail("customer@example.com");
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(new BigDecimal("100"));
        when(transactions.countByUserIdAndCreatedAtAfter(any(), any())).thenReturn(0L);
        when(rules.findByEnabledTrue()).thenReturn(List.of());
        when(evaluations.save(any(RiskEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RiskEvaluation evaluation = new RiskEvaluationService(evaluations, rules, transactions).evaluate(transaction);

        assertEquals(0, evaluation.getScore());
        assertEquals(RiskDecision.APPROVED, evaluation.getDecision());
        assertEquals("No risk indicators", evaluation.getReasons());
    }
}
