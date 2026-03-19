package com.masterlab.fraud_rule_engine.service;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.FraudAlert;
import com.masterlab.fraud_rule_engine.model.Transaction;
import com.masterlab.fraud_rule_engine.model.TransactionStatus;
import com.masterlab.fraud_rule_engine.repository.FraudAlertRepository;
import com.masterlab.fraud_rule_engine.rule.FraudRule;
import com.masterlab.fraud_rule_engine.rule.FraudRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {
    
    private final List<FraudRule> fraudRules;
    private final FraudAlertRepository fraudAlertRepository;
    
    @Transactional
    public void evaluateTransaction(Transaction transaction) {
        log.info("Evaluating transaction {} for fraud", transaction.getTransactionId());
        
        List<String> fraudReasons = new ArrayList<>();
        int totalRiskScore = 0;
        boolean isFraud = false;
        
        for (FraudRule rule : fraudRules) {
            FraudRuleResult result = rule.evaluate(transaction);
            
            if (result.isFlagged()) {
                isFraud = true;
                fraudReasons.add(result.getReason());
                totalRiskScore += result.getRiskScore();
                
                // Create fraud alert
                FraudAlert alert = FraudAlert.builder()
                    .transactionId(transaction.getId())
                    .ruleName(result.getRuleName())
                    .description(result.getReason())
                    .severity(result.getSeverity())
                    .riskScore(result.getRiskScore())
                    .build();
                
                fraudAlertRepository.save(alert);
                
                log.warn("Fraud detected - Rule: {}, Reason: {}", 
                    result.getRuleName(), result.getReason());
            }
        }
        
        transaction.setFlaggedAsFraud(isFraud);
        transaction.setRiskScore(Math.min(totalRiskScore, 100));
        transaction.setFraudReasons(String.join("; ", fraudReasons));
        
        if (isFraud) {
            if (totalRiskScore >= 70) {
                transaction.setStatus(TransactionStatus.DECLINED);
            } else {
                transaction.setStatus(TransactionStatus.UNDER_REVIEW);
            }
        } else {
            transaction.setStatus(TransactionStatus.APPROVED);
        }
        
        log.info("Transaction {} evaluation complete - Fraud: {}, Risk Score: {}, Status: {}", 
            transaction.getTransactionId(), isFraud, totalRiskScore, transaction.getStatus());
    }
}
