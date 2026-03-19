# Quick Start Guide

Get the Fraud Rule Engine running in under 5 minutes!

## Option 1: Docker (Recommended)

```bash
# Build and run with Docker Compose
cd fraud-rule-engine
docker-compose up --build

# Wait for "Started FraudRuleEngineApplication" message
# Application will be available at http://localhost:8080
```

## Option 2: Maven

```bash
# Run directly with Maven
cd fraud-rule-engine
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

## Verify Installation

Open your browser and navigate to:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## Test the API

### Using Swagger UI (Easiest)

1. Go to http://localhost:8080/swagger-ui.html
2. Expand "Transactions" section
3. Click "POST /api/v1/transactions"
4. Click "Try it out"
5. Use this sample data:

```json
{
  "transactionId": "TXN-TEST-001",
  "customerId": "CUST-123",
  "amount": 15000.00,
  "currency": "USD",
  "type": "PURCHASE",
  "category": "Electronics",
  "timestamp": "2026-03-13T14:30:00",
  "merchantId": "MERCH-456",
  "merchantName": "Tech Store",
  "location": "New York, NY"
}
```

6. Click "Execute"
7. Check the response - it should be flagged as fraud due to high amount!

### Using cURL

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-TEST-001",
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

### View Fraudulent Transactions

```bash
curl http://localhost:8080/api/v1/transactions/fraudulent
```

## What to Test

1. **Normal Transaction** (amount < $10,000) → Should be APPROVED
2. **High Amount** (amount > $10,000) → Should be flagged
3. **Multiple Rapid Transactions** (6+ in 60 min) → Should trigger velocity rule
4. **Night Transaction** (2 AM with amount > $2,000) → Should be flagged
5. **Round Amount** (e.g., $6,000) → Should trigger suspicious amount rule

## Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Explore the API using Swagger UI
- Check out [sample-requests.http](sample-requests.http) for more examples
- Review the code structure and fraud rules

## Troubleshooting

**Port 8080 already in use?**
```bash
# Change port in application.yaml
server:
  port: 8081
```

**Docker build fails?**
```bash
# Make sure Docker is running
docker --version

# Try building without cache
docker-compose build --no-cache
```

**Maven build fails?**
```bash
# Make sure Java 17 is installed
java -version

# Clean and rebuild
./mvnw clean install
```
