package com.masterlab.fraud_rule_engine.repository;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    
    List<FraudAlert> findByTransactionId(Long transactionId);
    
    List<FraudAlert> findBySeverity(AlertSeverity severity);
    
    List<FraudAlert> findBySeverityIn(List<AlertSeverity> severities);
}
