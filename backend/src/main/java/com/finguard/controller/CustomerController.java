package com.finguard.controller;

import com.finguard.entity.*;
import com.finguard.repository.*;
import com.finguard.service.TransactionService;
import com.finguard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final UserService users;
    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final RiskEvaluationRepository evaluations;
    private final TransactionService transactionService;

    public CustomerController(UserService users, AccountRepository accounts, TransactionRepository transactions, RiskEvaluationRepository evaluations, TransactionService transactionService) { this.users=users; this.accounts=accounts; this.transactions=transactions; this.evaluations=evaluations; this.transactionService=transactionService; }
    @GetMapping("/profile") public UserResponse profile(Authentication authentication) { return UserResponse.of(users.byEmail(authentication.getName())); }
    @GetMapping("/account") public AccountResponse account(Authentication authentication) { return AccountResponse.of(accountFor(authentication)); }
    @GetMapping("/account/balance") public BigDecimal balance(Authentication authentication) { return accountFor(authentication).getBalance(); }
    @PostMapping("/transactions/deposit") public TransactionResponse deposit(Authentication authentication,@Valid @RequestBody AmountRequest request) { return TransactionResponse.of(transactionService.deposit(userId(authentication),request.amount())); }
    @PostMapping("/transactions/withdraw") public TransactionResponse withdraw(Authentication authentication,@Valid @RequestBody AmountRequest request) { return TransactionResponse.of(transactionService.withdraw(userId(authentication),request.amount())); }
    @PostMapping("/transactions/transfer") public TransactionResponse transfer(Authentication authentication,@RequestHeader(value="Idempotency-Key",required=false) String key,@Valid @RequestBody TransferRequest request) { return TransactionResponse.of(transactionService.transfer(userId(authentication),request.destinationAccountNumber(),request.amount(),key)); }
    @GetMapping("/transactions") public List<TransactionResponse> transactionHistory(Authentication authentication) { return transactions.findTop20ByUserIdOrderByCreatedAtDesc(userId(authentication)).stream().map(TransactionResponse::of).toList(); }
    @GetMapping("/risk") public RiskResponse risk(Authentication authentication) {
        UUID userId = userId(authentication);
        List<RiskEvaluation> recent = evaluations.findTop20ByTransactionUserIdOrderByEvaluatedAtDesc(userId);
        int score = recent.stream().mapToInt(RiskEvaluation::getScore).max().orElse(0);
        RiskDecision decision = score <= 30 ? RiskDecision.APPROVED : score <= 60 ? RiskDecision.REVIEW : RiskDecision.BLOCKED;
        return RiskResponse.of(score, decision, recent,
                transactions.countByUserIdAndStatus(userId, TransactionStatus.APPROVED) + transactions.countByUserIdAndStatus(userId, TransactionStatus.COMPLETED),
                transactions.countByUserIdAndStatus(userId, TransactionStatus.REVIEW),
                transactions.countByUserIdAndStatus(userId, TransactionStatus.BLOCKED));
    }
    @GetMapping("/transactions/{id}") public TransactionResponse transaction(Authentication authentication,@PathVariable UUID id) { return TransactionResponse.of(transactions.findByIdAndUserId(id,userId(authentication)).orElseThrow(()->new IllegalArgumentException("Transaction not found"))); }
    private UUID userId(Authentication authentication){return users.byEmail(authentication.getName()).getId();}
    private Account accountFor(Authentication authentication){return accounts.findByUserId(userId(authentication)).orElseThrow(()->new IllegalArgumentException("Account not found"));}
    public record AmountRequest(@NotNull BigDecimal amount,String description,String location) {}
    public record TransferRequest(@NotBlank String destinationAccountNumber,@NotNull BigDecimal amount,String description,String location) {}
    public record UserResponse(UUID id,String name,String email,UserRole role){static UserResponse of(User u){return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole());}}
    public record AccountResponse(UUID id,String accountNumber,BigDecimal balance,String currency,boolean active){static AccountResponse of(Account a){return new AccountResponse(a.getId(),a.getAccountNumber(),a.getBalance(),a.getCurrency(),a.isActive());}}
    public record TransactionResponse(UUID id,String reference,TransactionType type,TransactionStatus status,BigDecimal amount,String description,String location,java.time.Instant createdAt){static TransactionResponse of(Transaction t){return new TransactionResponse(t.getId(),t.getTransactionReference(),t.getType(),t.getStatus(),t.getAmount(),t.getDescription(),t.getLocation(),t.getCreatedAt());}}
    public record RiskResponse(int riskScore,String riskLevel,List<RiskEvaluationResponse> recentEvaluations,long suspiciousTransactionCount,Map<String,Long> summary) {
        static RiskResponse of(int score, RiskDecision decision, List<RiskEvaluation> evaluations, long approved, long review, long blocked) {
            List<RiskEvaluationResponse> recent = evaluations.stream().map(RiskEvaluationResponse::of).toList();
            return new RiskResponse(score, decision == RiskDecision.APPROVED ? "LOW" : decision == RiskDecision.REVIEW ? "MEDIUM" : "HIGH", recent, review + blocked, Map.of("approved", approved, "review", review, "blocked", blocked));
        }
    }
    public record RiskEvaluationResponse(UUID transactionId,String transactionReference,int score,String decision,String riskReasons,BigDecimal amount,TransactionType type,java.time.Instant evaluatedAt) {
        static RiskEvaluationResponse of(RiskEvaluation evaluation) { Transaction transaction = evaluation.getTransaction(); return new RiskEvaluationResponse(transaction.getId(), transaction.getTransactionReference(), evaluation.getScore(), evaluation.getDecision().name(), evaluation.getReasons(), transaction.getAmount(), transaction.getType(), evaluation.getEvaluatedAt()); }
    }
}