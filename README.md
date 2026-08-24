# Payment Support Platform

A lightweight **Kotlin + Spring Boot backend** designed to simulate a production payment-support environment.

The project focuses on the engineering and troubleshooting workflow behind customer-facing payment systems: tracing API requests through application layers, investigating database state, reproducing issues, identifying root causes, and implementing fixes.

> **Project focus:** Production support, troubleshooting, backend fundamentals, API investigation, SQL/database analysis, and payment-system concepts.

---

## 🎯 Why I Built This

I built this project to gain practical exposure to a Kotlin/Spring backend and, more importantly, to practice the type of investigation involved in production support engineering.

The project was intentionally kept small so that the focus remains on understanding the system and troubleshooting it rather than building unnecessary features.

### Main objectives

- Understand Kotlin and Spring Boot application structure
- Learn the Controller → Service → Repository architecture
- Build and consume REST APIs
- Work with PostgreSQL and Spring Data JPA
- Investigate customer-facing API issues
- Compare API responses with underlying database state
- Trace issues through application code
- Practice root-cause analysis
- Understand data consistency problems in transactional systems
- Work with Docker and GitHub Codespaces
- Document technical findings clearly

---

# 🏗️ Architecture

The application follows a simple layered backend architecture:

```text
                    HTTP Request
                         │
                         ▼
                ┌─────────────────┐
                │   Controller    │
                │                 │
                │ Receives HTTP   │
                │ requests        │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │    Service      │
                │                 │
                │ Business logic  │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │   Repository    │
                │                 │
                │ Database access │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │   PostgreSQL    │
                │                 │
                │ Persistent data │
                └─────────────────┘
```

### Controller

The controller is responsible for handling HTTP requests and returning HTTP responses.

For example:

```text
POST /api/transactions
```

is received by `TransactionController`.

### Service

The service layer contains the business logic.

For example, `TransactionService` handles transaction creation and updating the associated account balance.

### Repository

Repositories use Spring Data JPA to communicate with PostgreSQL.

For example:

```kotlin
accountRepository.findById(accountId)
```

retrieves an account from the database.

### PostgreSQL

PostgreSQL stores account and transaction information.

---

# 🧩 Project Structure

```text
payment-support-platform/
│
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── hammad/
│       │           └── payment_support_platform/
│       │               │
│       │               ├── PaymentSupportPlatformApplication.kt
│       │               ├── HealthController.kt
│       │               │
│       │               ├── Account.kt
│       │               ├── AccountController.kt
│       │               ├── AccountService.kt
│       │               ├── AccountRepository.kt
│       │               │
│       │               ├── Transaction.kt
│       │               ├── TransactionController.kt
│       │               ├── TransactionService.kt
│       │               └── TransactionRepository.kt
│       │
│       └── resources/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

---

# 💳 Core Domain

The application currently models two basic concepts.

## Account

An account contains:

- Account ID
- Customer name
- Phone number
- Balance
- Account status

Example:

```json
{
  "id": 1,
  "customerName": "Hammad Fazal",
  "phoneNumber": "03001234567",
  "balance": 5000,
  "status": "ACTIVE"
}
```

## Transaction

A transaction contains:

- Transaction ID
- Account ID
- Amount
- Transaction type
- Transaction status
- Creation timestamp

Example:

```json
{
  "id": 1,
  "accountId": 1,
  "amount": 5000,
  "type": "CREDIT",
  "status": "COMPLETED"
}
```

---

# 🔌 API Endpoints

## Health Check

### Request

```http
GET /api/health
```

### Response

```json
{
  "status": "UP",
  "service": "payment-support-platform"
}
```

---

## Create Account

### Request

```http
POST /api/accounts
Content-Type: application/json
```

### Example

```json
{
  "customerName": "Hammad Fazal",
  "phoneNumber": "03001234567"
}
```

### Example Response

```json
{
  "id": 1,
  "customerName": "Hammad Fazal",
  "phoneNumber": "03001234567",
  "balance": 0,
  "status": "ACTIVE"
}
```

---

## Get Account

### Request

```http
GET /api/accounts/{id}
```

Example:

```bash
curl http://localhost:8080/api/accounts/1
```

### Response

```json
{
  "id": 1,
  "customerName": "Hammad Fazal",
  "phoneNumber": "03001234567",
  "balance": 5000,
  "status": "ACTIVE"
}
```

---

## Create Transaction

### Request

```http
POST /api/transactions
Content-Type: application/json
```

### Example

```json
{
  "accountId": 1,
  "amount": 5000,
  "type": "CREDIT"
}
```

### Example Response

```json
{
  "id": 1,
  "accountId": 1,
  "amount": 5000,
  "type": "CREDIT",
  "status": "COMPLETED"
}
```

---

# 🔍 Production Troubleshooting Exercises

A major purpose of this project was to practice investigating issues in the same way a production/support engineer would investigate a customer-facing problem.

Rather than assuming the cause of an issue, the investigation follows:

```text
Customer report
      │
      ▼
Reproduce issue
      │
      ▼
Inspect HTTP response
      │
      ▼
Check application logs
      │
      ▼
Trace request through code
      │
      ▼
Query database
      │
      ▼
Compare expected vs actual state
      │
      ▼
Identify root cause
      │
      ▼
Implement / recommend fix
      │
      ▼
Retest
```

---

# 🐛 Incident 1 — Missing Account Returned HTTP 500

### Customer scenario

A customer/account lookup fails for account ID `999`.

### Reproduction

```bash
curl -i http://localhost:8080/api/accounts/999
```

### Initial behavior

The API returned:

```text
HTTP 500 Internal Server Error
```

### Investigation

The account lookup used:

```kotlin
accountRepository.findById(id)
    .orElseThrow {
        RuntimeException("Account $id not found")
    }
```

The database correctly contained no record for the requested account.

However, the application converted this expected "not found" condition into an unhandled runtime exception.

### Root Cause

A missing resource was being represented as a generic server error.

### Correct behavior

The behavior was corrected to return:

```text
HTTP 404 Not Found
```

This demonstrates an important distinction during production troubleshooting:

```text
404 → Requested resource does not exist

500 → Unexpected server-side failure
```

---

# 🐛 Incident 2 — Transaction / Balance Inconsistency

### Customer scenario

A customer completes a Rs. 5,000 credit transaction, but their account balance remains unchanged.

### Reproduction

A transaction was created:

```json
{
  "accountId": 1,
  "amount": 5000,
  "type": "CREDIT"
}
```

The transaction appeared successfully in the database.

However:

```http
GET /api/accounts/1
```

continued to return:

```json
{
  "balance": 0
}
```

### Investigation

The investigation compared:

```text
API response
      │
      ├── Account balance = 0
      │
      ▼
Transaction database record
      │
      ├── Amount = 5000
      │
      ▼
TransactionService
```

The service was persisting the transaction but was not updating the associated account balance.

### Root Cause

The transaction persistence and account balance update were not being handled together.

### Fix

The transaction service was updated to:

1. Retrieve the account
2. Calculate the new balance
3. Save the updated account
4. Save the transaction

This demonstrated the importance of investigating both application behavior and underlying database state when troubleshooting payment-related inconsistencies.

---

# 🧠 Production Support Mindset

The project follows an evidence-first troubleshooting approach.

Instead of assuming:

> "The customer's account is probably blocked."

The investigation should establish what actually happened.

For example:

```text
Customer reports incorrect balance
             │
             ▼
Check API response
             │
             ▼
Check account table
             │
             ▼
Check transaction table
             │
             ▼
Check application logs
             │
             ▼
Trace relevant service code
             │
             ▼
Determine actual root cause
```

The goal is to distinguish between:

- What the customer reports
- What the API says
- What the database contains
- What the application actually did
- What the available evidence proves

---

# 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Gradle | Build and dependency management |
| Docker | Containerized development |
| Git | Version control |
| GitHub | Source control and collaboration |
| GitHub Codespaces | Cloud development environment |

---

# 🚀 Running the Project

## Prerequisites

The project can be developed using GitHub Codespaces.

Required tools:

- Java 21
- Docker
- Git
- Gradle Wrapper

The repository includes the Gradle Wrapper, so a separate Gradle installation is not required.

---

## Clone the Repository

```bash
git clone https://github.com/hammad-fazal/payment-support-platform.git
cd payment-support-platform
```

---

## Start the Application

```bash
./gradlew bootRun
```

The application runs on:

```text
http://localhost:8080
```

---

## Test the Health Endpoint

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "UP",
  "service": "payment-support-platform"
}
```

---

# 🗄️ Database Investigation

When investigating an issue, database state can be checked directly using PostgreSQL.

For example:

```sql
SELECT * FROM account;
```

Find a specific account:

```sql
SELECT id, customer_name, balance, status
FROM account
WHERE id = 1;
```

Inspect transactions:

```sql
SELECT *
FROM transactions;
```

Find transactions belonging to an account:

```sql
SELECT *
FROM transactions
WHERE account_id = 1;
```

This allows API behavior to be compared directly with persistent database state.

---

# 🔄 Example End-to-End Flow

## Creating a Transaction

```text
POST /api/transactions
        │
        ▼
TransactionController
        │
        ▼
TransactionService
        │
        ├── Find Account
        │
        ├── Calculate Balance
        │
        ├── Save Account
        │
        └── Save Transaction
                │
                ▼
        TransactionRepository
                │
                ▼
           PostgreSQL
```

## Retrieving an Account

```text
GET /api/accounts/1
        │
        ▼
AccountController
        │
        ▼
AccountService
        │
        ▼
AccountRepository
        │
        ▼
PostgreSQL
        │
        ▼
Account
        │
        ▼
JSON Response
```

---

# 📌 What This Project Demonstrates

This project demonstrates practical experience with:

- Reading and navigating an unfamiliar Kotlin/Spring codebase
- Understanding layered backend architecture
- REST API troubleshooting
- SQL/database investigation
- Identifying API/database inconsistencies
- Reproducing customer-facing issues
- Root-cause analysis
- Handling HTTP error conditions
- Understanding basic transactional workflows
- Using Docker-based development environments
- Documenting technical incidents and findings

---

# 🔮 Potential Future Improvements

The project is intentionally lightweight.

Possible extensions include:

- Centralized exception handling using `@ControllerAdvice`
- Automated unit and integration tests
- Transaction history endpoint
- Account transaction reconciliation
- Structured application logging
- Monitoring and alerting
- Idempotency handling for payment requests
- Database transactions using `@Transactional`
- Authentication and authorization
- Automated support investigation tooling
- CI/CD pipeline
- Kubernetes deployment

These are intentionally listed as future improvements rather than implemented functionality.

---

# 👨‍💻 Author

**Hammad Fazal**

Telecommunications Engineering graduate with experience in production support, SQL, Python, Linux, Docker, troubleshooting, and technical operations.

This project represents my hands-on exploration of Kotlin/Spring backend systems and production support workflows in a simulated payment/fintech environment.

---

## Disclaimer

This is an educational project and does not process real financial transactions or customer data.

All payment scenarios and account information used in the project are simulated.
