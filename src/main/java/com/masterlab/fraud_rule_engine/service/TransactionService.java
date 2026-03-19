package com.masterlab.fraud_rule_engine.service;

import com.masterlab.fraud_rule_engine.dto.TransactionRequest;
import com.masterlab.fraud_rule_engine.dto.TransactionResponse;
import com.masterlab.fraud_rule_engine.exception.ResourceNotFoundException;
import com.masterlab.fraud_rule_engine.model.Transaction;
import com.masterlab.fraud_rule_engine.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;
    
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        log.info("Processing transaction: {}", request.getTransactionId());
        
        Transaction transaction = Transaction.builder()
            .transactionId(request.getTransactionId())
            .customerId(request.getCustomerId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .type(request.getType())
            .category(request.getCategory())
            .timestamp(request.getTimestamp())
            .merchantId(request.getMerchantId())
            .merchantName(request.getMerchantName())
            .location(request.getLocation())
            .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        fraudDetectionService.evaluateTransaction(savedTransaction);
        
        Transaction updatedTransaction = transactionRepository.save(savedTransaction);
        
        return mapToResponse(updatedTransaction);
    }
    
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return mapToResponse(transaction);
    }
    
    public TransactionResponse getTransactionByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with transactionId: " + transactionId));
        return mapToResponse(transaction);
    }
    
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getTransactionsByCustomerId(String customerId) {
        return transactionRepository.findByCustomerId(customerId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getFraudulentTransactions() {
        return transactionRepository.findByFlaggedAsFraudTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        List<String> reasons = transaction.getFraudReasons() != null && !transaction.getFraudReasons().isEmpty()
            ? Arrays.asList(transaction.getFraudReasons().split("; "))
            : List.of();
        
        return TransactionResponse.builder()
            .id(transaction.getId())
            .transactionId(transaction.getTransactionId())
            .customerId(transaction.getCustomerId())
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .type(transaction.getType())
            .category(transaction.getCategory())
            .timestamp(transaction.getTimestamp())
            .merchantId(transaction.getMerchantId())
            .merchantName(transaction.getMerchantName())
            .location(transaction.getLocation())
            .status(transaction.getStatus())
            .flaggedAsFraud(transaction.getFlaggedAsFraud())
            .riskScore(transaction.getRiskScore())
            .fraudReasons(reasons)
            .createdAt(transaction.getCreatedAt())
            .build();
    }
}
