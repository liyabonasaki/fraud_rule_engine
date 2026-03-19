package com.masterlab.fraud_rule_engine.dto;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertResponse {
    private Long id;
    private Long transactionId;
    private String ruleName;
    private String description;
    private AlertSeverity severity;
    private Integer riskScore;
    private LocalDateTime createdAt;
}
