package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NightTimeTransactionRule implements FraudRule {
    
    private static final int NIGHT_START_HOUR = 23;
    private static final int NIGHT_END_HOUR = 5;
    private static final BigDecimal NIGHT_AMOUNT_THRESHOLD = new BigDecimal("2000.00");
    
    @Override
    public FraudRuleResult evaluate(Transaction transaction) {
        int hour = transaction.getTimestamp().getHour();
        boolean isNightTime = hour >= NIGHT_START_HOUR || hour < NIGHT_END_HOUR;
        
        if (isNightTime && transaction.getAmount().compareTo(NIGHT_AMOUNT_THRESHOLD) > 0) {
            return FraudRuleResult.builder()
                .flagged(true)
                .ruleName(getRuleName())
                .reason(String.format("High-value transaction (%.2f) during night hours (%d:00)", 
                    transaction.getAmount(), hour))
                .severity(AlertSeverity.MEDIUM)
                .riskScore(getRiskScore())
                .build();
        }
        return FraudRuleResult.builder().flagged(false).build();
    }
    
    @Override
    public String getRuleName() {
        return "NIGHT_TIME_TRANSACTION";
    }
    
    @Override
    public int getRiskScore() {
        return 20;
    }
}
