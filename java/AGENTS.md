# AGENTS.md - Time Deposit Refactoring Kata

## Project Overview
XA Bank Time Deposit API — a Spring Boot application for managing time deposit accounts with interest calculations.

## Tech Stack
- **Java 21**, **Spring Boot 4.0.5**, **Maven**
- **PostgreSQL** (prod), **H2** (test), **Flyway** (migrations)
- **OpenAPI/Swagger** (springdoc + openapi-generator, delegate pattern)
- **Testcontainers** (PostgreSQL 16-alpine for integration tests)
- **JUnit 5**, **AssertJ**, **Mockito**

## Architecture: Hexagonal (Ports & Adapters)

```
domain/          — Pure domain logic (no framework deps)
application/     — Use cases + port interfaces
infrastructure/  — Adapters (REST, JPA, Spring config)
```

### Domain Layer (`org.ikigaidigital.domain`)
- **`TimeDeposit`** — Mutable POJO: `id`, `planType`, `balance`, `days`. **DO NOT modify this class or the `updateBalance` method signature.**
- **`Withdrawal`** — Record: `id`, `timeDepositId`, `amount`, `date`
- **`TimeDepositPlan`** — Interface: `double interestUnrounded(TimeDeposit)`
- **`AbstractTimeDepositPlan`** — Enforces 30-day grace period; subclasses implement `interestAfterGrace()`
- **Plan implementations**: `BasicTimeDepositPlan` (1%), `StudentTimeDepositPlan` (3%, no interest after 365 days), `PremiumTimeDepositPlan` (5%, interest starts after 45 days), `NoInterestTimeDepositPlan` (fallback)
- **`TimeDepositPlanFactory`** — Maps plan type strings to singleton plan instances
- **`TimeDepositCalculator`** — Iterates deposits, calculates interest, rounds HALF_UP to 2 decimals, updates balance

### Application Layer (`org.ikigaidigital.application`)
- **Port**: `TimeDepositRepository` — `findAllDeposits()`, `findAllWithWithdrawals()`, `saveAll()`
- **Use Cases**: `GetAllTimeDepositsUseCase`, `UpdateBalancesUseCase`
- **Model**: `TimeDepositView` — Record combining `TimeDeposit` + `List<Withdrawal>`

### Infrastructure Layer (`org.ikigaidigital.infrastructure`)
- **REST**: `TimeDepositController` — Implements generated `TimeDepositsApi` interface
- **Persistence**: `TimeDepositJpaAdapter` (implements port), JPA entities + Spring Data repos
- **Config**: `ApplicationConfig` — Bean definitions

## API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/time-deposits` | List all deposits with withdrawals |
| POST | `/time-deposits/update-balances` | Recalculate and update all balances |

OpenAPI spec: `src/main/resources/openapi.yaml`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Database Schema
- **`time_deposits`**: `id` (PK), `plan_type`, `balance` (NUMERIC), `days`
- **`withdrawals`**: `id` (PK), `time_deposit_id` (FK), `amount`, `date`

Migration: `src/main/resources/db/migration/V1__Initial_schema.sql`

## Running the App
```bash
# Option 1: Docker Compose (recommended)
docker-compose up

# Option 2: Manual (requires local PostgreSQL)
./start-dev.sh

# Option 3: Maven directly (requires local PostgreSQL + Flyway migrated)
mvn spring-boot:run
```

## Running Tests
```bash
mvn test          # Unit + integration tests
```

## Key Constraints
- **No breaking changes** to `TimeDeposit` class or `updateBalance` method signature
- **Exactly two API endpoints** — do not add more
- `TimeDepositCalculator.updateBalance` behavior must remain unchanged
- Use OpenAPI contract-first, Hexagonal Architecture, Testcontainers

## Interest Rules
| Plan | Rate | Special Rule |
|------|------|-------------|
| Basic | 1% | No interest first 30 days |
| Student | 3% | No interest first 30 days; zero interest after 365 days |
| Premium | 5% | No interest first 30 days; interest starts after 45 days |

Formula: `balance * rate / 12` (monthly), rounded HALF_UP to 2 decimals
