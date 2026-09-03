package com.finguard.repository;
import com.finguard.entity.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FraudRuleRepository extends JpaRepository<FraudRule, UUID> { List<FraudRule> findByEnabledTrue(); }
