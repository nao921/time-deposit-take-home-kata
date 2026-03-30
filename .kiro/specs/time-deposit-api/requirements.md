# Requirements Document

## Introduction

XA Bank requires a RESTful API layer on top of an existing time deposit domain model. A junior developer implemented the core interest calculation logic (`TimeDepositCalculator`) but did not wire it into a web API or persistence layer. This spec covers refactoring the project into a Spring Boot application with a database, two REST endpoints, and proper test coverage — without breaking the existing `TimeDeposit` class or `updateBalance` method signature.

## Glossary

- **TimeDeposit**: A financial product with a plan type, balance, and number of days held.
- **PlanType**: One of `basic`, `student`, or `premium`, each with distinct interest rules.
- **TimeDepositCalculator**: The existing class whose `updateBalance(List<TimeDeposit>)` method signature must remain unchanged.
- **Withdrawal**: A record of a withdrawal event associated with a time deposit, with an amount and date.
- **Interest**: Monthly interest applied to a time deposit balance based on plan type and days held.
- **API**: The Spring Boot REST layer exposing the two required endpoints.
- **Repository**: The persistence layer responsible for reading and writing time deposit and withdrawal data.

---

## Requirements

### Requirement 1: Project Setup and Spring Boot Migration

**User Story:** As a developer, I want the Java project to be a Spring Boot application, so that I can run it as a standalone service with dependency injection and auto-configuration.

#### Acceptance Criteria

1. THE System SHALL use Spring Boot 3.x as the application framework.
2. THE System SHALL target Java 21.
3. THE System SHALL use Maven as the build tool with all required dependencies declared in `pom.xml`.
4. THE System SHALL include the Spring Web, Spring Data JPA, and OpenAPI/Swagger dependencies.
5. THE System SHALL include Testcontainers and JUnit 5 with AssertJ as test dependencies.

---

### Requirement 2: Database Schema

**User Story:** As a developer, I want time deposits and withdrawals stored in a relational database, so that data persists across application restarts.

#### Acceptance Criteria

1. THE System SHALL define a `timeDeposits` table with columns: `id` (Integer, primary key), `planType` (String, required), `days` (Integer, required), `balance` (Decimal, required).
2. THE System SHALL define a `withdrawals` table with columns: `id` (Integer, primary key), `timeDepositId` (Integer, foreign key referencing `timeDeposits.id`, required), `amount` (Decimal, required), `date` (Date, required).
3. THE System SHALL enforce the foreign key relationship between `withdrawals.timeDepositId` and `timeDeposits.id`.
4. WHEN the application starts, THE System SHALL initialise the database schema automatically.

---

### Requirement 3: Domain Model Preservation

**User Story:** As a developer, I want the existing `TimeDeposit` class and `TimeDepositCalculator.updateBalance` method to remain unchanged, so that no breaking changes are introduced to the shared domain logic.

#### Acceptance Criteria

1. THE System SHALL NOT modify the constructor signature of `TimeDeposit(int id, String planType, Double balance, int days)`.
2. THE System SHALL NOT modify the method signature of `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`.
3. THE System SHALL NOT alter the interest calculation logic inside `updateBalance`.
4. WHERE a JPA entity is needed, THE System SHALL introduce a separate entity class rather than modifying `TimeDeposit`.

---

### Requirement 4: Retrieve All Time Deposits Endpoint

**User Story:** As an API consumer, I want to retrieve all time deposits with their associated withdrawals, so that I can view the current state of all accounts.

#### Acceptance Criteria

1. THE API SHALL expose a `GET /time-deposits` endpoint.
2. WHEN the endpoint is called, THE API SHALL return HTTP 200 with a JSON array of all time deposits.
3. THE API SHALL include the following fields in each response item: `id`, `planType`, `balance`, `days`, `withdrawals`.
4. THE `withdrawals` field SHALL be a list of withdrawal objects, each containing `id`, `timeDepositId`, `amount`, and `date`.
5. WHEN no time deposits exist, THE API SHALL return HTTP 200 with an empty JSON array.

---

### Requirement 5: Update All Balances Endpoint

**User Story:** As an API consumer, I want to trigger a balance update for all time deposits, so that monthly interest is applied to every account.

#### Acceptance Criteria

1. THE API SHALL expose a `POST /time-deposits/update-balances` endpoint.
2. WHEN the endpoint is called, THE API SHALL load all time deposits from the database.
3. WHEN the endpoint is called, THE API SHALL invoke `TimeDepositCalculator.updateBalance` with the loaded time deposits.
4. WHEN the endpoint is called, THE API SHALL persist the updated balances back to the database.
5. WHEN the endpoint is called, THE API SHALL return HTTP 200.

---

### Requirement 6: Interest Calculation Rules

**User Story:** As a bank operator, I want interest calculated correctly per plan type, so that customers receive the right returns.

#### Acceptance Criteria

1. WHEN `days` is 30 or fewer, THE System SHALL apply zero interest regardless of plan type.
2. WHEN `planType` is `basic` and `days` is greater than 30, THE System SHALL apply 1% annual interest divided by 12 per month.
3. WHEN `planType` is `student` and `days` is greater than 30 and fewer than 366, THE System SHALL apply 3% annual interest divided by 12 per month.
4. WHEN `planType` is `student` and `days` is 366 or more, THE System SHALL apply zero interest.
5. WHEN `planType` is `premium` and `days` is greater than 45, THE System SHALL apply 5% annual interest divided by 12 per month.
6. WHEN `planType` is `premium` and `days` is 45 or fewer, THE System SHALL apply zero interest.

---

### Requirement 7: OpenAPI / Swagger Documentation

**User Story:** As an API consumer, I want an OpenAPI Swagger contract available, so that I can discover and trigger the endpoints without reading source code.

#### Acceptance Criteria

1. THE System SHALL expose a Swagger UI at `/swagger-ui.html` or `/swagger-ui/index.html`.
2. THE System SHALL expose an OpenAPI JSON/YAML spec at `/v3/api-docs`.
3. THE API SHALL document both endpoints (`GET /time-deposits` and `POST /time-deposits/update-balances`) in the OpenAPI spec.

---

### Requirement 8: Hexagonal Architecture

**User Story:** As a developer, I want the codebase to follow Hexagonal Architecture, so that the domain logic is decoupled from infrastructure concerns.

#### Acceptance Criteria

1. THE System SHALL separate code into at least three layers: `domain`, `application` (use cases / ports), and `infrastructure` (adapters: REST controllers, JPA repositories).
2. WHEN infrastructure implementations are changed, THE domain and application layers SHALL remain unaffected.
3. THE domain layer SHALL NOT depend on Spring or JPA annotations.

---

### Requirement 9: Integration Tests with Testcontainers

**User Story:** As a developer, I want integration tests that run against a real database in a container, so that I can verify the full request-to-database flow.

#### Acceptance Criteria

1. THE Test Suite SHALL use Testcontainers to spin up a real database instance for integration tests.
2. WHEN the `POST /time-deposits/update-balances` endpoint is called in an integration test, THE Test Suite SHALL verify that balances are updated in the database.
3. WHEN the `GET /time-deposits` endpoint is called in an integration test, THE Test Suite SHALL verify that the correct time deposit data is returned.
