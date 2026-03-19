package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.Transaction;
import com.masterlab.fraud_rule_engine.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VelocityRule implements FraudRule {
    
    private final TransactionRepository transactionRepository;
    
    @Value("${fraud.rules.velocity-check-window-minutes}")
    private int velocityWindowMinutes;
    
    @Value("${fraud.rules.velocity-max-transactions}")
    private int maxTransactions;
    
    @Override
    public FraudRuleResult evaluate(Transaction transaction) {
        LocalDateTime windowStart = transaction.getTimestamp().minusMinutes(velocityWindowMinutes);
        LocalDateTime windowEnd = transaction.getTimestamp();
        
        Long transactionCount = transactionRepository.countByCustomerIdAndTimestampBetween(
            transaction.getCustomerId(), windowStart, windowEnd
        );
        
        if (transactionCount >= maxTransactions) {
            return FraudRuleResult.builder()
                .flagged(true)
                .ruleName(getRuleName())
                .reason(String.format("Customer has %d transactions in %d minutes (max: %d)", 
                    transactionCount + 1, velocityWindowMinutes, maxTransactions))
                .severity(AlertSeverity.CRITICAL)
                .riskScore(getRiskScore())
                .build();
        }
        return FraudRuleResult.builder().flagged(false).build();
    }
    
    @Override
    public String getRuleName() {
        return "VELOCITY_CHECK";
    }
    
    @Override
    public int getRiskScore() {
        return 50;
    }
}
