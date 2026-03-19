package com.masterlab.fraud_rule_engine.dto;

import com.masterlab.fraud_rule_engine.model.TransactionStatus;
import com.masterlab.fraud_rule_engine.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private String category;
    private LocalDateTime timestamp;
    private String merchantId;
    private String merchantName;
    private String location;
    private TransactionStatus status;
    private Boolean flaggedAsFraud;
    private Integer riskScore;
    private List<String> fraudReasons;
    private LocalDateTime createdAt;
}
