package com.finguard.controller;

import com.finguard.entity.*;
import com.finguard.repository.*;
import com.finguard.service.TransactionService;
import com.finguard.service.UserService;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyst")
public class AnalystController {
    private final TransactionRepository transactions;
    private final RiskEvaluationRepository evaluations;
    private final AuditLogRepository auditLogs;
    private final UserService users;
    private final TransactionService transactionService;

    public AnalystController(TransactionRepository transactions, RiskEvaluationRepository evaluations, AuditLogRepository auditLogs, UserService users, TransactionService transactionService) {
        this.transactions = transactions; this.evaluations = evaluations; this.auditLogs = auditLogs; this.users = users; this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Transaction> review = transactions.findByStatusOrderByCreatedAtDesc(TransactionStatus.REVIEW);
        return Map.of("underReview", review.size(), "blocked", transactions.countByStatus(TransactionStatus.BLOCKED),
                "approved", transactions.countByStatus(TransactionStatus.APPROVED), "recentSuspicious", review.stream().limit(10).map(this::view).toList());
    }

    @GetMapping("/suspicious-transactions")
    public List<Map<String, Object>> suspicious() {
        return transactions.findByStatusOrderByCreatedAtDesc(TransactionStatus.REVIEW)
                .stream().map(this::view).toList();
    }

    @GetMapping("/transactions/{id}")
    public Map<String, Object> detail(@PathVariable UUID id) { return view(transaction(id)); }

    @PostMapping("/transactions/{id}/approve")
    @Transactional
    public Map<String, Object> approve(@PathVariable UUID id, @RequestBody(required=false) DecisionRequest request, Authentication authentication) { return decide(id, TransactionStatus.COMPLETED, "APPROVE", request == null ? "" : request.notes(), authentication); }

    @PostMapping("/transactions/{id}/reject")
    @Transactional
    public Map<String, Object> reject(@PathVariable UUID id, @RequestBody(required=false) DecisionRequest request, Authentication authentication) { return decide(id, TransactionStatus.BLOCKED, "BLOCK", request == null ? "" : request.notes(), authentication); }

    private Map<String, Object> decide(UUID id, TransactionStatus status, String action, String notes, Authentication authentication) {
        Transaction transaction = transaction(id);
        if (transaction.getStatus() != TransactionStatus.REVIEW) throw new IllegalArgumentException("Only review transactions can be decided");
        if (status == TransactionStatus.COMPLETED) transaction = transactionService.approveReviewed(id); else { transaction.setStatus(status); transaction = transactions.save(transaction); }
            AuditLog log = new AuditLog(); log.setUser(users.byEmail(authentication.getName())); log.setAction(action); log.setEntityType("Transaction"); log.setEntityId(id); log.setDescription(notes == null || notes.isBlank() ? status.name() : status.name() + ": " + notes.trim()); auditLogs.save(log);
        return view(transaction);
    }

    private Transaction transaction(UUID id) { return transactions.findById(id).orElseThrow(() -> new IllegalArgumentException("Transaction not found")); }
    private Map<String, Object> view(Transaction t) {
        Optional<RiskEvaluation> risk = evaluations.findByTransactionId(t.getId());
        Map<String, Object> result = new LinkedHashMap<>(); result.put("id", t.getId()); result.put("reference", t.getTransactionReference());
        result.put("type", t.getType()); result.put("status", t.getStatus()); result.put("amount", t.getAmount()); result.put("createdAt", t.getCreatedAt());
        result.put("customer", t.getUser().getEmail()); risk.ifPresent(value -> { result.put("riskScore", value.getScore()); result.put("riskDecision", value.getDecision()); result.put("riskReasons", value.getReasons()); }); return result;
    }
    public record DecisionRequest(String notes) {}
}
