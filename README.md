# Fraud Rule Engine Service

A production-grade fraud detection system that processes financial transaction events and flags potential fraud using configurable rule-based detection.

## Features

- **Real-time Fraud Detection**: Processes transactions and applies multiple fraud detection rules
- **Configurable Rules Engine**: Extensible rule-based system with multiple detection strategies
- **RESTful API**: Comprehensive API for transaction processing and fraud alert retrieval
- **Persistent Storage**: JPA-based data persistence with H2 (development) and PostgreSQL support
- **Risk Scoring**: Calculates cumulative risk scores based on triggered rules
- **Alert Management**: Categorizes fraud alerts by severity (LOW, MEDIUM, HIGH, CRITICAL)
- **API Documentation**: Interactive Swagger UI for API exploration
- **Production Ready**: Includes Docker support, health checks, and proper error handling

## Fraud Detection Rules

The system implements the following fraud detection rules:

1. **High Amount Rule**: Flags transactions exceeding a configurable threshold (default: $10,000)
2. **Velocity Rule**: Detects suspicious transaction frequency (default: 5+ transactions in 60 minutes)
3. **Suspicious Amount Rule**: Identifies round-number transactions above threshold (e.g., $5,000, $6,000)
4. **Night Time Transaction Rule**: Flags high-value transactions during night hours (11 PM - 5 AM)

Each rule contributes to an overall risk score, and transactions are automatically:
- **APPROVED**: Risk score < 70, no fraud detected
- **UNDER_REVIEW**: Risk score 70-99, potential fraud
- **DECLINED**: Risk score ≥ 100, high fraud probability

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **PostgreSQL** (production-ready)
- **Lombok** (reduces boilerplate)
- **SpringDoc OpenAPI** (API documentation)
- **Maven** (build tool)
- **Docker** (containerization)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)

## Building the Project

### Using Maven

```bash
cd fraud-rule-engine
mvnw clean package
```

### Using Docker

```bash
cd fraud-rule-engine
docker build -t fraud-rule-engine:latest .
```

## Running the Application

### Option 1: Run with Maven

```bash
mvnw spring-boot:run
```

### Option 2: Run the JAR

```bash
java -jar target/fraud-rule-engine-0.0.1-SNAPSHOT.jar
```

### Option 3: Run with Docker

```bash
docker run -p 8080:8080 fraud-rule-engine:latest
```

### Option 4: Run with Docker Compose

```bash
docker-compose up
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

## API Endpoints

### Transaction Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/transactions` | Process a new transaction |
| GET | `/api/v1/transactions` | Get all transactions |
| GET | `/api/v1/transactions/{id}` | Get transaction by ID |
| GET | `/api/v1/transactions/transaction/{transactionId}` | Get transaction by transaction ID |
| GET | `/api/v1/transactions/customer/{customerId}` | Get all transactions for a customer |
| GET | `/api/v1/transactions/fraudulent` | Get all fraudulent transactions |

### Fraud Alert Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/fraud-alerts` | Get all fraud alerts |
| GET | `/api/v1/fraud-alerts/transaction/{transactionId}` | Get alerts for a transaction |
| GET | `/api/v1/fraud-alerts/severity/{severity}` | Get alerts by severity |
| GET | `/api/v1/fraud-alerts/high-priority` | Get HIGH and CRITICAL alerts |

## Example Usage

### Process a Transaction

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-001",
    "customerId": "CUST-123",
    "amount": 15000.00,
    "currency": "USD",
    "type": "PURCHASE",
    "category": "Electronics",
    "timestamp": "2026-03-13T14:30:00",
    "merchantId": "MERCH-456",
    "merchantName": "Tech Store",
    "location": "New York, NY"
  }'
```

### Get Fraudulent Transactions

```bash
curl http://localhost:8080/api/v1/transactions/fraudulent
```

### Get High Priority Alerts

```bash
curl http://localhost:8080/api/v1/fraud-alerts/high-priority
```

## Testing

### Run Unit Tests

```bash
mvnw test
```

### Manual Testing with Sample Data

The application includes a REST API that can be tested using:
- Swagger UI (http://localhost:8080/swagger-ui.html)
- Postman
- cURL commands (see examples above)

### Test Scenarios

1. **Normal Transaction** (should be approved):
   - Amount: $500
   - Time: 2 PM
   - Result: APPROVED, no fraud flags

2. **High Amount Transaction** (should trigger HIGH_AMOUNT rule):
   - Amount: $15,000
   - Result: UNDER_REVIEW or DECLINED

3. **Velocity Attack** (should trigger VELOCITY_CHECK rule):
   - Submit 6+ transactions for same customer within 60 minutes
   - Result: DECLINED

4. **Night Transaction** (should trigger NIGHT_TIME_TRANSACTION rule):
   - Amount: $3,000
   - Time: 2 AM
   - Result: UNDER_REVIEW

## Configuration

Key configuration properties in `application.yaml`:

```yaml
fraud:
  rules:
    high-amount-threshold: 10000.00
    velocity-check-window-minutes: 60
    velocity-max-transactions: 5
    suspicious-amount-threshold: 5000.00
    daily-limit: 50000.00
```

## Database

### H2 Console (Development)

Access the H2 console at: http://localhost:8080/h2-console

- **JDBC URL**: `jdbc:h2:mem:frauddb`
- **Username**: `sa`
- **Password**: (leave empty)

### PostgreSQL (Production)

Update `application.yaml` to use PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/frauddb
    username: your_username
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## Health Checks

- **Health Endpoint**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## Architecture

### Project Structure

```
src/main/java/com/masterlab/fraud_rule_engine/
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── exception/           # Exception handling
├── model/               # JPA entities
├── repository/          # Data access layer
├── rule/                # Fraud detection rules
└── service/             # Business logic
```

### Design Patterns

- **Strategy Pattern**: Fraud rules implement a common interface
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Separation of API and domain models
- **Builder Pattern**: Lombok builders for object construction

## Production Considerations

- **Security**: Add Spring Security for authentication/authorization
- **Rate Limiting**: Implement API rate limiting
- **Monitoring**: Integrate with Prometheus/Grafana
- **Logging**: Configure centralized logging (ELK stack)
- **Database**: Use PostgreSQL or MySQL for production
- **Caching**: Add Redis for performance optimization
- **Message Queue**: Consider Kafka/RabbitMQ for async processing
- **Circuit Breaker**: Add Resilience4j for fault tolerance

## Future Enhancements

- Machine learning-based fraud detection
- Real-time streaming with Kafka
- Geographic location-based rules
- Customer behavior profiling
- Whitelist/blacklist management
- Admin dashboard for rule configuration
- Notification system for fraud alerts
- Integration with external fraud databases

## License

This project is created for evaluation purposes.

## Author

Created as part of a technical assessment for a backend developer position.
