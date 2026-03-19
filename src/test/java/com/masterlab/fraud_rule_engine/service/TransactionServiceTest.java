package com.masterlab.fraud_rule_engine.service;

import com.masterlab.fraud_rule_engine.dto.TransactionRequest;
import com.masterlab.fraud_rule_engine.dto.TransactionResponse;
import com.masterlab.fraud_rule_engine.model.Transaction;
import com.masterlab.fraud_rule_engine.model.TransactionType;
import com.masterlab.fraud_rule_engine.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private FraudDetectionService fraudDetectionService;
    
    @InjectMocks
    private TransactionService transactionService;
    
    @Test
    void shouldProcessTransactionSuccessfully() {
        TransactionRequest request = TransactionRequest.builder()
            .transactionId("TXN-001")
            .customerId("CUST-123")
            .amount(new BigDecimal("500.00"))
            .currency("USD")
            .type(TransactionType.PURCHASE)
            .category("Groceries")
            .timestamp(LocalDateTime.now())
            .build();
        
        Transaction savedTransaction = Transaction.builder()
            .id(1L)
            .transactionId(request.getTransactionId())
            .customerId(request.getCustomerId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .type(request.getType())
            .category(request.getCategory())
            .timestamp(request.getTimestamp())
            .flaggedAsFraud(false)
            .riskScore(0)
            .createdAt(LocalDateTime.now())
            .build();
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        doNothing().when(fraudDetectionService).evaluateTransaction(any(Transaction.class));
        
        TransactionResponse response = transactionService.processTransaction(request);
        
        assertNotNull(response);
        assertEquals("TXN-001", response.getTransactionId());
        assertEquals("CUST-123", response.getCustomerId());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(fraudDetectionService, times(1)).evaluateTransaction(any(Transaction.class));
    }
}
