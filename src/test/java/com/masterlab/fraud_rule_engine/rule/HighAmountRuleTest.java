package com.masterlab.fraud_rule_engine.rule;

import com.masterlab.fraud_rule_engine.model.AlertSeverity;
import com.masterlab.fraud_rule_engine.model.Transaction;
import com.masterlab.fraud_rule_engine.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HighAmountRuleTest {
    
    private HighAmountRule rule;
    
    @BeforeEach
    void setUp() {
        rule = new HighAmountRule();
        ReflectionTestUtils.setField(rule, "highAmountThreshold", new BigDecimal("10000.00"));
    }
    
    @Test
    void shouldFlagTransactionAboveThreshold() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN-001")
            .customerId("CUST-123")
            .amount(new BigDecimal("15000.00"))
            .currency("USD")
            .type(TransactionType.PURCHASE)
            .category("Electronics")
            .timestamp(LocalDateTime.now())
            .build();
        
        FraudRuleResult result = rule.evaluate(transaction);
        
        assertTrue(result.isFlagged());
        assertEquals("HIGH_AMOUNT", result.getRuleName());
        assertEquals(AlertSeverity.HIGH, result.getSeverity());
        assertEquals(40, result.getRiskScore());
    }
    
    @Test
    void shouldNotFlagTransactionBelowThreshold() {
        Transaction transaction = Transaction.builder()
            .transactionId("TXN-002")
            .customerId("CUST-123")
            .amount(new BigDecimal("5000.00"))
            .currency("USD")
            .type(TransactionType.PURCHASE)
            .category("Electronics")
            .timestamp(LocalDateTime.now())
            .build();
        
        FraudRuleResult result = rule.evaluate(transaction);
        
        assertFalse(result.isFlagged());
    }
}
