package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.Transaction;

public interface FraudRule {
    
    FraudRuleResult evaluate(Transaction transaction);
    
    String getRuleName();
    
    int getRiskScore();
}
