# Architecture Overview

## System Summary

The Fraud Rule Engine is a Spring Boot REST service that processes financial transactions in real-time, evaluates them against a set of configurable fraud detection rules, and persists both transactions and any generated fraud alerts.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17 |
| Framework | Spring Boot 3.5.x |
| Persistence | Spring Data JPA + H2 (dev) / PostgreSQL (prod) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 2.8.x (Swagger UI) |
| Build | Maven |
| Containerization | Docker / Docker Compose |

---

## Package Structure

```
com.masterlab.fraud_rule_engine/
├── config/          # OpenAPI/Swagger configuration
├── controller/      # REST controllers (HTTP layer)
├── dto/             # Request/Response DTOs
├── exception/       # Global exception handling
├── model/           # JPA entities and enums
├── repository/      # Spring Data JPA repositories
├── rule/            # Fraud detection rules (Strategy pattern)
└── service/         # Business logic
```

---

## Request Flow

```
HTTP Request
    │
    ▼
TransactionController
    │  POST /api/v1/transactions
    ▼
TransactionService
    │  1. Maps DTO → Transaction entity
    │  2. Persists transaction (status: PENDING)
    │  3. Delegates to FraudDetectionService
    ▼
FraudDetectionService
    │  Iterates over all FraudRule beans
    │  For each triggered rule:
    │    - Creates and persists a FraudAlert
    │    - Accumulates risk score and reasons
    │  Sets final transaction status
    ▼
TransactionRepository / FraudAlertRepository
    │  Persists updated transaction + alerts
    ▼
TransactionResponse (mapped from entity)
```

---

## Domain Model

### Transaction
Core entity representing a financial transaction.

| Field | Type | Notes |
|---|---|---|
| id | Long | PK, auto-generated |
| transactionId | String | Business identifier |
| customerId | String | Indexed |
| amount | BigDecimal | |
| currency | String | 3-char ISO code |
| type | TransactionType | PURCHASE, WITHDRAWAL, TRANSFER, PAYMENT, REFUND, DEPOSIT |
| timestamp | LocalDateTime | Indexed |
| status | TransactionStatus | PENDING → APPROVED / UNDER_REVIEW / DECLINED |
| flaggedAsFraud | Boolean | |
| riskScore | Integer | 0–100, capped |
| fraudReasons | String | Semicolon-delimited rule reasons |

### FraudAlert
One record per triggered rule per transaction.

| Field | Type | Notes |
|---|---|---|
| id | Long | PK |
| transactionId | Long | FK to Transaction.id |
| ruleName | String | e.g. HIGH_AMOUNT |
| description | String | Human-readable reason |
| severity | AlertSeverity | LOW / MEDIUM / HIGH / CRITICAL |
| riskScore | Integer | Rule's contribution |

### Enums

- `TransactionStatus`: PENDING, APPROVED, UNDER_REVIEW, DECLINED
- `TransactionType`: PURCHASE, WITHDRAWAL, TRANSFER, PAYMENT, REFUND, DEPOSIT
- `AlertSeverity`: LOW, MEDIUM, HIGH, CRITICAL

---

## Fraud Rules (Strategy Pattern)

All rules implement the `FraudRule` interface:

```java
public interface FraudRule {
    FraudRuleResult evaluate(Transaction transaction);
    String getRuleName();
    int getRiskScore();
}
```

Spring auto-collects all `FraudRule` beans into a `List<FraudRule>` injected into `FraudDetectionService`.

| Rule | Trigger Condition | Severity | Risk Score |
|---|---|---|---|
| `HIGH_AMOUNT` | amount > $10,000 | HIGH | 40 |
| `VELOCITY_CHECK` | >= 5 transactions in 60 min for same customer | CRITICAL | 50 |
| `SUSPICIOUS_AMOUNT` | amount > $5,000 and is a round $1,000 multiple | MEDIUM | 25 |
| `NIGHT_TIME_TRANSACTION` | amount > $2,000 between 23:00-05:00 | MEDIUM | 20 |

### Risk Score to Transaction Status

| Total Risk Score | Status |
|---|---|
| < 70, no flags | APPROVED |
| flagged, score < 70 | UNDER_REVIEW |
| flagged, score >= 70 | DECLINED |

Risk score is capped at 100.

---

## API Endpoints

### Transactions - TransactionController

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/transactions` | Submit transaction for fraud evaluation |
| GET | `/api/v1/transactions` | List all transactions |
| GET | `/api/v1/transactions/{id}` | Get by database ID |
| GET | `/api/v1/transactions/transaction/{transactionId}` | Get by business transaction ID |
| GET | `/api/v1/transactions/customer/{customerId}` | Get all for a customer |
| GET | `/api/v1/transactions/fraudulent` | Get all flagged transactions |

### Fraud Alerts - FraudAlertController

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/fraud-alerts` | List all alerts |
| GET | `/api/v1/fraud-alerts/transaction/{transactionId}` | Alerts for a transaction |
| GET | `/api/v1/fraud-alerts/severity/{severity}` | Filter by severity |
| GET | `/api/v1/fraud-alerts/high-priority` | HIGH + CRITICAL alerts only |

---

## Error Handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) handles:

| Exception | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `MethodArgumentNotValidException` | 400 Bad Request (with field-level errors) |
| `Exception` (catch-all) | 500 Internal Server Error |

All error responses follow the `ErrorResponse` structure with `timestamp`, `status`, `error`, `message`, and optional `validationErrors`.

---

## Data Access

### TransactionRepository
- `findByTransactionId(String)` - lookup by business ID
- `findByCustomerId(String)` - customer transaction history
- `findByFlaggedAsFraudTrue()` - all fraudulent transactions
- `countByCustomerIdAndTimestampBetween(...)` - used by `VelocityRule` to count recent transactions

### FraudAlertRepository
- `findByTransactionId(Long)` - alerts for a transaction
- `findBySeverity(AlertSeverity)` - filter by severity
- `findBySeverityIn(List<AlertSeverity>)` - used for high-priority alert queries

---

## Configuration

Key properties in `application.yaml`:

```yaml
fraud:
  rules:
    high-amount-threshold: 10000.00
    velocity-check-window-minutes: 60
    velocity-max-transactions: 5
    suspicious-amount-threshold: 5000.00
    daily-limit: 50000.00
```

---

## Database

- Development: H2 in-memory (`jdbc:h2:mem:frauddb`), console at `/h2-console`
- Production: PostgreSQL (update datasource config in `application.yaml`)
- Schema managed by Hibernate (`ddl-auto: create-drop` in dev)

---

## Extending the Rules Engine

To add a new fraud rule:

1. Create a class implementing `FraudRule` and annotate it with `@Component`
2. Implement `evaluate()`, `getRuleName()`, and `getRiskScore()`
3. Add any configurable thresholds to `application.yaml`

Spring will automatically pick it up - no wiring needed.
