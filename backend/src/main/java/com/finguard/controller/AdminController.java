package com.finguard.controller;

import com.finguard.entity.*;
import com.finguard.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final AccountRepository accounts; private final TransactionRepository transactions;
    private final FraudRuleRepository rules; private final AuditLogRepository auditLogs;
    public AdminController(UserRepository users, AccountRepository accounts, TransactionRepository transactions, FraudRuleRepository rules, AuditLogRepository auditLogs) {
        this.users=users; this.accounts=accounts; this.transactions=transactions; this.rules=rules; this.auditLogs=auditLogs;
    }
    @GetMapping("/dashboard") public Map<String,Object> dashboard() { return Map.of("totalUsers",users.count(),"totalAccounts",accounts.count(),"totalTransactions",transactions.count(),"blockedTransactions",transactions.countByStatus(TransactionStatus.BLOCKED),"reviewTransactions",transactions.countByStatus(TransactionStatus.REVIEW),"activeFraudRules",rules.findByEnabledTrue().size()); }
    @GetMapping("/statistics") public Map<String,Object> statistics() { return dashboard(); }
    @GetMapping("/users") public List<UserView> userList() { return users.findAll().stream().map(UserView::of).toList(); }
    @GetMapping("/users/{id}") public UserView user(@PathVariable UUID id) { return UserView.of(users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"))); }
    @GetMapping("/accounts") public List<AccountView> accountList() { return accounts.findAll().stream().map(AccountView::of).toList(); }
    @GetMapping("/transactions") public List<TransactionView> transactionList() { return transactions.findAll().stream().map(TransactionView::of).toList(); }
    @GetMapping("/fraud-rules") public List<FraudRule> ruleList() { return rules.findAll(); }
    @PostMapping("/fraud-rules") public FraudRule createRule(@Valid @RequestBody RuleRequest request) { FraudRule rule=new FraudRule(); rule.setName(request.name()); rule.setDescription(request.description()); rule.setRuleType(request.ruleType()); rule.setScore(request.score()); return rules.save(rule); }
    @PutMapping("/fraud-rules/{id}") public FraudRule updateRule(@PathVariable UUID id,@Valid @RequestBody RuleRequest request) { FraudRule rule=rules.findById(id).orElseThrow(() -> new IllegalArgumentException("Fraud rule not found")); rule.setName(request.name()); rule.setDescription(request.description()); rule.setRuleType(request.ruleType()); rule.setScore(request.score()); return rules.save(rule); }
    @PatchMapping("/fraud-rules/{id}/status") public FraudRule status(@PathVariable UUID id,@RequestParam boolean enabled) { FraudRule rule=rules.findById(id).orElseThrow(() -> new IllegalArgumentException("Fraud rule not found")); rule.setEnabled(enabled); return rules.save(rule); }
    @GetMapping("/audit-logs") public List<AuditView> auditLogs() { return auditLogs.findAll().stream().map(AuditView::of).toList(); }
    public record RuleRequest(@NotBlank String name,@NotBlank String ruleType,String description,@Min(0) @Max(100) int score) {}
    public record UserView(UUID id,String name,String email,UserRole role,boolean enabled,java.time.Instant createdAt) { static UserView of(User u){return new UserView(u.getId(),u.getName(),u.getEmail(),u.getRole(),u.isEnabled(),u.getCreatedAt());} }
    public record AccountView(UUID id,String accountNumber,String email,java.math.BigDecimal balance,String currency,boolean active) { static AccountView of(Account a){return new AccountView(a.getId(),a.getAccountNumber(),a.getUser().getEmail(),a.getBalance(),a.getCurrency(),a.isActive());} }
    public record TransactionView(UUID id,String reference,TransactionType type,TransactionStatus status,java.math.BigDecimal amount,java.time.Instant createdAt) { static TransactionView of(Transaction t){return new TransactionView(t.getId(),t.getTransactionReference(),t.getType(),t.getStatus(),t.getAmount(),t.getCreatedAt());} }
    public record AuditView(UUID id,String action,String entityType,UUID entityId,String description,java.time.Instant createdAt) { static AuditView of(AuditLog l){return new AuditView(l.getId(),l.getAction(),l.getEntityType(),l.getEntityId(),l.getDescription(),l.getCreatedAt());} }
}
