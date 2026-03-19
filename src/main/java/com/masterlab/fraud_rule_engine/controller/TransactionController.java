package com.masterlab.fraud_rule_engine.controller;

import com.masterlab.fraud_rule_engine.dto.TransactionRequest;
import com.masterlab.fraud_rule_engine.dto.TransactionResponse;
import com.masterlab.fraud_rule_engine.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction processing and retrieval endpoints")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    @Operation(summary = "Process a new transaction", description = "Submit a transaction for fraud detection analysis")
    public ResponseEntity<TransactionResponse> processTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.processTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve all processed transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its database ID")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get transaction by transaction ID", description = "Retrieve a specific transaction by its transaction ID")
    public ResponseEntity<TransactionResponse> getTransactionByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionByTransactionId(transactionId));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get transactions by customer", description = "Retrieve all transactions for a specific customer")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomerId(customerId));
    }
    
    @GetMapping("/fraudulent")
    @Operation(summary = "Get fraudulent transactions", description = "Retrieve all transactions flagged as fraudulent")
    public ResponseEntity<List<TransactionResponse>> getFraudulentTransactions() {
        return ResponseEntity.ok(transactionService.getFraudulentTransactions());
    }
}
