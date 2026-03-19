package com.masterlab.fraud_rule_engine.service;

import com.masterlab.fraud_rule_engine.dto.FraudAlertResponse;
import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.FraudAlert;
import com.masterlab.fraud_rule_engine.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudAlertService {
    
    private final FraudAlertRepository fraudAlertRepository;
    
    public List<FraudAlertResponse> getAllAlerts() {
        return fraudAlertRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<FraudAlertResponse> getAlertsByTransactionId(Long transactionId) {
        return fraudAlertRepository.findByTransactionId(transactionId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<FraudAlertResponse> getAlertsBySeverity(AlertSeverity severity) {
        return fraudAlertRepository.findBySeverity(severity).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<FraudAlertResponse> getHighPriorityAlerts() {
        List<AlertSeverity> highPriority = List.of(AlertSeverity.HIGH, AlertSeverity.CRITICAL);
        return fraudAlertRepository.findBySeverityIn(highPriority).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private FraudAlertResponse mapToResponse(FraudAlert alert) {
        return FraudAlertResponse.builder()
            .id(alert.getId())
            .transactionId(alert.getTransactionId())
            .ruleName(alert.getRuleName())
            .description(alert.getDescription())
            .severity(alert.getSeverity())
            .riskScore(alert.getRiskScore())
            .createdAt(alert.getCreatedAt())
            .build();
    }
}
