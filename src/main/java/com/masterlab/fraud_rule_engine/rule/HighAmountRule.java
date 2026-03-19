package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighAmountRule implements FraudRule {
    
    @Value("${fraud.rules.high-amount-threshold}")
    private BigDecimal highAmountThreshold;
    
    @Override
    public FraudRuleResult evaluate(Transaction transaction) {
        if (transaction.getAmount().compareTo(highAmountThreshold) > 0) {
            return FraudRuleResult.builder()
                .flagged(true)
                .ruleName(getRuleName())
                .reason(String.format("Transaction amount %.2f exceeds threshold %.2f", 
                    transaction.getAmount(), highAmountThreshold))
                .severity(AlertSeverity.HIGH)
                .riskScore(getRiskScore())
                .build();
        }
        return FraudRuleResult.builder().flagged(false).build();
    }
    
    @Override
    public String getRuleName() {
        return "HIGH_AMOUNT";
    }
    
    @Override
    public int getRiskScore() {
        return 40;
    }
}
