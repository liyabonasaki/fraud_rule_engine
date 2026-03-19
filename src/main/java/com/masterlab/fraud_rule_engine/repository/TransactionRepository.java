package com.masterlab.fraud_rule_engine.repository;

import com.masterlab.fraud_rule_engine.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByCustomerId(String customerId);
    
    List<Transaction> findByFlaggedAsFraudTrue();
    
    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId " +
           "AND t.timestamp BETWEEN :startTime AND :endTime")
    List<Transaction> findByCustomerIdAndTimestampBetween(
        @Param("customerId") String customerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.customerId = :customerId " +
           "AND t.timestamp BETWEEN :startTime AND :endTime")
    Long countByCustomerIdAndTimestampBetween(
        @Param("customerId") String customerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
