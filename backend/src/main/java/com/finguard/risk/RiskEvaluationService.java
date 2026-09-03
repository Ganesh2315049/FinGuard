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
    private final RiskEvaluationRepository evaluations; private final FraudRuleRepository rules;
    public RiskEvaluationService(RiskEvaluationRepository evaluations, FraudRuleRepository rules) { this.evaluations = evaluations; this.rules = rules; }
    public RiskEvaluation evaluate(Transaction transaction) {
        int score=0; List<String> reasons=new ArrayList<>(); BigDecimal amount=transaction.getAmount();
        if (amount.compareTo(new BigDecimal("100000")) >= 0) { score+=30; reasons.add("High transaction amount"); }
        int hour=transaction.getCreatedAt().atZone(ZoneOffset.UTC).getHour();
        if (hour<5) { score+=15; reasons.add("Unusual transaction time"); }
        int configuredBonus=rules.findByEnabledTrue().stream().filter(r -> "AMOUNT_THRESHOLD".equals(r.getRuleType()) && amount.compareTo(new BigDecimal("50000"))>=0).mapToInt(FraudRule::getScore).sum();
        if (configuredBonus>0) { score=Math.min(100,score+configuredBonus); reasons.add("Enabled fraud rule matched"); }
        RiskEvaluation evaluation=new RiskEvaluation(); evaluation.setTransaction(transaction); evaluation.setScore(Math.min(score,100)); evaluation.setDecision(score<=30?RiskDecision.APPROVED:score<=60?RiskDecision.REVIEW:RiskDecision.BLOCKED); evaluation.setReasons(reasons.isEmpty()?"No risk indicators":String.join("; ", reasons)); return evaluations.save(evaluation);
    }
}
