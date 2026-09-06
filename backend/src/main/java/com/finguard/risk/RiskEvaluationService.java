package com.finguard.risk;

import com.finguard.entity.*;
import com.finguard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
public class RiskEvaluationService {
    private final RiskEvaluationRepository evaluations; private final FraudRuleRepository rules; private final TransactionRepository transactions;
    public RiskEvaluationService(RiskEvaluationRepository evaluations, FraudRuleRepository rules, TransactionRepository transactions) { this.evaluations = evaluations; this.rules = rules; this.transactions = transactions; }
    public RiskEvaluation evaluate(Transaction transaction) {
        int score=0; List<String> reasons=new ArrayList<>(); BigDecimal amount=transaction.getAmount();
        if (amount.compareTo(new BigDecimal("60000")) > 0) { score+=70; reasons.add("High transaction amount"); }
        else if (amount.compareTo(new BigDecimal("30000")) > 0) { score+=40; reasons.add("Elevated transaction amount"); }
        int hour=transaction.getCreatedAt().atZone(ZoneOffset.UTC).getHour();
        if (hour<5) { score+=15; reasons.add("Unusual transaction time"); }
        if (transactions.countByUserIdAndCreatedAtAfter(transaction.getUser().getId(), transaction.getCreatedAt().minus(Duration.ofMinutes(10))) > 3) { score+=25; reasons.add("Multiple transactions in a short period"); }
        int configuredBonus=rules.findByEnabledTrue().stream().filter(r -> "AMOUNT_THRESHOLD".equals(r.getRuleType()) && amount.compareTo(new BigDecimal("30000"))>0).mapToInt(FraudRule::getScore).sum();
        if (configuredBonus>0) { score=Math.min(100,score+configuredBonus); reasons.add("Enabled fraud rule matched"); }
        RiskEvaluation evaluation=new RiskEvaluation(); evaluation.setTransaction(transaction); evaluation.setScore(Math.min(score,100)); evaluation.setDecision(score<=30?RiskDecision.APPROVED:score<=60?RiskDecision.REVIEW:RiskDecision.BLOCKED); evaluation.setReasons(reasons.isEmpty()?"No risk indicators":String.join("; ", reasons)); return evaluations.save(evaluation);
    }
}
