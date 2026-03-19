package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SuspiciousAmountRule implements FraudRule {
    
    @Value("${fraud.rules.suspicious-amount-threshold}")
    private BigDecimal suspiciousThreshold;
    
    @Override
    public FraudRuleResult evaluate(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        
        // Check for round numbers that are suspiciously high
        if (amount.compareTo(suspiciousThreshold) > 0 && 
            amount.remainder(BigDecimal.valueOf(1000)).compareTo(BigDecimal.ZERO) == 0) {
            return FraudRuleResult.builder()
                .flagged(true)
                .ruleName(getRuleName())
                .reason(String.format("Suspicious round amount: %.2f", amount))
                .severity(AlertSeverity.MEDIUM)
                .riskScore(getRiskScore())
                .build();
        }
        return FraudRuleResult.builder().flagged(false).build();
    }
    
    @Override
    public String getRuleName() {
        return "SUSPICIOUS_AMOUNT";
    }
    
    @Override
    public int getRiskScore() {
        return 25;
    }
}
