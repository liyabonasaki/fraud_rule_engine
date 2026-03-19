package com.masterlab.fraud_rule_engine.controller;

import com.masterlab.fraud_rule_engine.dto.FraudAlertResponse;
import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.service.FraudAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud-alerts")
@RequiredArgsConstructor
@Tag(name = "Fraud Alerts", description = "Fraud alert retrieval endpoints")
public class FraudAlertController {
    
    private final FraudAlertService fraudAlertService;
    
    @GetMapping
    @Operation(summary = "Get all fraud alerts", description = "Retrieve all fraud alerts in the system")
    public ResponseEntity<List<FraudAlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(fraudAlertService.getAllAlerts());
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get alerts by transaction", description = "Retrieve all fraud alerts for a specific transaction")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByTransactionId(@PathVariable Long transactionId) {
        return ResponseEntity.ok(fraudAlertService.getAlertsByTransactionId(transactionId));
    }
    
    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get alerts by severity", description = "Retrieve fraud alerts filtered by severity level")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsBySeverity(@PathVariable AlertSeverity severity) {
        return ResponseEntity.ok(fraudAlertService.getAlertsBySeverity(severity));
    }
    
    @GetMapping("/high-priority")
    @Operation(summary = "Get high priority alerts", description = "Retrieve all HIGH and CRITICAL severity fraud alerts")
    public ResponseEntity<List<FraudAlertResponse>> getHighPriorityAlerts() {
        return ResponseEntity.ok(fraudAlertService.getHighPriorityAlerts());
    }
}
