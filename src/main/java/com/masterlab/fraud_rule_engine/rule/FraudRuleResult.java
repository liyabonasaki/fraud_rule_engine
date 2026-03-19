package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleResult {
    private boolean flagged;
    private String ruleName;
    private String reason;
    private AlertSeverity severity;
    private int riskScore;
}
