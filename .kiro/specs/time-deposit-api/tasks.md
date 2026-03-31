# Implementation Plan: time-deposit-api

## Overview

Migrate the existing Maven/Java project to a Spring Boot 3.x application following Hexagonal Architecture. Introduce PostgreSQL persistence, two REST endpoints, OpenAPI docs, and full test coverage (unit, property-based with jqwik, integration with Testcontainers) — without modifying `TimeDeposit` or `TimeDepositCalculator`.

## Tasks

- [x] 1. Migrate pom.xml to Spring Boot 3.x / Java 21
  - Replace the current `pom.xml` with a Spring Boot 3.x parent, Java 21 compiler settings, and all required dependencies: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `postgresql` (runtime), `springdoc-openapi-starter-webmvc-ui`, `spring-boot-starter-test`, `testcontainers` (postgresql + junit-jupiter), `jqwik`, `archunit-junit5`
  - Add the `spring-boot-maven-plugin` and configure `maven-surefire-plugin` to include jqwik's engine
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [x] 2. Create Spring Boot application entry point and base package structure
  - Create `org.ikigaidigital.TimeDepositApiApplication` with `@SpringBootApplication` in `java/src/main/java/org/ikigaidigital/`
  - Create empty package directories for `domain`, `application.port`, `application.usecase`, `application.model`, `infrastructure.rest.dto`, `infrastructure.persistence`
  - Create `src/main/resources/application.properties` with datasource placeholders and `spring.jpa.hibernate.ddl-auto=validate`
  - Create `src/main/resources/schema.sql` with the DDL for `time_deposits` and `withdrawals` tables
  - _Requirements: 1.1, 1.2, 2.1, 2.2, 2.3, 2.4_

- [x] 3. Implement domain layer — Withdrawal value object
  - Create `org.ikigaidigital.domain.Withdrawal` as a Java record: `(int id, int timeDepositId, Double amount, LocalDate date)`
  - Move or copy `TimeDeposit` and `TimeDepositCalculator` references into the domain layer without modifying their source; the originals in `org.ikigaidigital` remain untouched
  - _Requirements: 3.1, 3.2, 3.4, 8.1, 8.3_

- [x] 4. Implement application layer — port, model, and use cases
  - [x] 4.1 Create `TimeDepositRepository` port interface
    - Create `org.ikigaidigital.application.port.TimeDepositRepository` with `List<TimeDeposit> findAll()` and `void saveAll(List<TimeDeposit> deposits)`
    - _Requirements: 8.1, 8.2_

  - [x] 4.2 Create `TimeDepositView` application model
    - Create `org.ikigaidigital.application.model.TimeDepositView` as a record: `(TimeDeposit deposit, List<Withdrawal> withdrawals)`
    - _Requirements: 4.3, 4.4, 8.1_

  - [x] 4.3 Implement `GetAllTimeDepositsUseCase`
    - Create `org.ikigaidigital.application.usecase.GetAllTimeDepositsUseCase` annotated with `@Service`
    - Inject `TimeDepositRepository` port; `execute()` returns `List<TimeDepositView>`
    - _Requirements: 4.1, 4.2, 4.3, 4.4_

  - [x] 4.4 Implement `UpdateBalancesUseCase`
    - Create `org.ikigaidigital.application.usecase.UpdateBalancesUseCase` annotated with `@Service`
    - Inject `TimeDepositRepository` port and `TimeDepositCalculator`; `execute()` loads → `updateBalance` → `saveAll`
    - _Requirements: 5.2, 5.3, 5.4_

  - [x]* 4.5 Write unit tests for use-case orchestration
    - Mock `TimeDepositRepository` port; verify `GetAllTimeDepositsUseCase.execute()` delegates to `findAll()` and maps to `TimeDepositView`
    - Verify `UpdateBalancesUseCase.execute()` calls `findAll()`, then `updateBalance`, then `saveAll()` with updated deposits
    - _Requirements: 4.2, 5.2, 5.3, 5.4_

- [x] 5. Implement infrastructure persistence layer
  - [x] 5.1 Create JPA entities `TimeDepositEntity` and `WithdrawalEntity`
    - `TimeDepositEntity`: `@Entity @Table(name="time_deposits")`, fields `id`, `planType`, `days`, `balance` (BigDecimal), `@OneToMany(fetch=EAGER)` withdrawals
    - `WithdrawalEntity`: `@Entity @Table(name="withdrawals")`, fields `id`, `amount`, `date`, `@ManyToOne` timeDeposit
    - _Requirements: 2.1, 2.2, 2.3, 3.4_

  - [x] 5.2 Create Spring Data repositories
    - Create `TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer>`
    - Create `WithdrawalJpaRepository extends JpaRepository<WithdrawalEntity, Integer>`
    - _Requirements: 2.1, 2.2_

  - [x] 5.3 Implement `TimeDepositJpaAdapter`
    - Create `org.ikigaidigital.infrastructure.persistence.TimeDepositJpaAdapter implements TimeDepositRepository`
    - Implement `findAll()`: load entities, map to `TimeDeposit` domain objects (adapter also returns withdrawal data via a separate query path used by the controller)
    - Implement `saveAll()`: map `TimeDeposit` back to entities and call `saveAll` on the JPA repository
    - Add a `findAllWithWithdrawals()` method returning `List<TimeDepositView>` for the GET use case
    - _Requirements: 3.4, 5.4, 8.1, 8.2_

  - [x]* 5.4 Write unit tests for mapper logic
    - Test `TimeDepositEntity` → `TimeDeposit` mapping (all fields preserved)
    - Test `TimeDeposit` → `TimeDepositEntity` mapping (balance update round-trip)
    - Test `WithdrawalEntity` → `Withdrawal` mapping
    - _Requirements: 3.1, 3.4_

- [x] 6. Implement infrastructure REST layer
  - [x] 6.1 Create response DTOs
    - Create `org.ikigaidigital.infrastructure.rest.dto.WithdrawalResponse` record: `(int id, int timeDepositId, Double amount, LocalDate date)`
    - Create `org.ikigaidigital.infrastructure.rest.dto.TimeDepositResponse` record: `(int id, String planType, Double balance, int days, List<WithdrawalResponse> withdrawals)`
    - _Requirements: 4.3, 4.4_

  - [x] 6.2 Implement `TimeDepositController`
    - Create `org.ikigaidigital.infrastructure.rest.TimeDepositController` with `@RestController @RequestMapping("/time-deposits")`
    - `@GetMapping` → calls `GetAllTimeDepositsUseCase`, maps `List<TimeDepositView>` to `List<TimeDepositResponse>`, returns `ResponseEntity<List<TimeDepositResponse>>` with HTTP 200
    - `@PostMapping("/update-balances")` → calls `UpdateBalancesUseCase`, returns `ResponseEntity<Void>` with HTTP 200
    - _Requirements: 4.1, 4.2, 4.5, 5.1, 5.5_

  - [x]* 6.3 Write MockMvc unit tests for the controller
    - Test `GET /time-deposits` returns HTTP 200 with correct JSON structure (id, planType, balance, days, withdrawals array)
    - Test `GET /time-deposits` with empty repository returns HTTP 200 with `[]`
    - Test `POST /time-deposits/update-balances` returns HTTP 200
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.5_

- [ ] 7. Checkpoint — Ensure all unit tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Add OpenAPI / Swagger documentation
  - Verify `springdoc-openapi-starter-webmvc-ui` is on the classpath (added in task 1)
  - Add `@Operation` annotations on both controller methods describing their purpose
  - Add `application.properties` entry `springdoc.api-docs.path=/v3/api-docs` if not default
  - _Requirements: 7.1, 7.2, 7.3_

  - [x]* 8.1 Write test verifying OpenAPI endpoints are reachable
    - Use MockMvc to assert `GET /swagger-ui/index.html` returns HTTP 200
    - Use MockMvc to assert `GET /v3/api-docs` returns HTTP 200 with JSON content
    - _Requirements: 7.1, 7.2_

- [ ] 9. Add ArchUnit architecture enforcement test
  - Create `org.ikigaidigital.ArchitectureTest` using `@AnalyzeClasses` and `@ArchTest`
  - Assert that no class in `org.ikigaidigital.domain` imports from `org.springframework` or `jakarta.persistence`
  - Assert that `infrastructure` classes do not appear in `domain` or `application` packages
  - _Requirements: 8.1, 8.2, 8.3_

- [ ] 10. Implement property-based tests with jqwik
  - [ ] 10.1 Write property test for Property 1 — GET returns all deposits with correct shape
    - // Feature: time-deposit-api, Property 1: GET endpoint returns all deposits with correct shape
    - Use `@Property` with jqwik arbitraries to generate random `TimeDepositEntity` lists, seed the DB via the JPA repository, call `GET /time-deposits`, assert response length equals seeded count and each element has `id`, `planType`, `balance`, `days`, `withdrawals`
    - **Validates: Requirements 4.2, 4.3, 4.4, 9.3**

  - [ ] 10.2 Write property test for Property 2 — update-balances persists calculator result
    - // Feature: time-deposit-api, Property 2: Update-balances persists correct interest to the database
    - Generate random deposits, seed DB, call `POST /time-deposits/update-balances`, reload from DB, compare each balance with direct `TimeDepositCalculator.updateBalance` output
    - **Validates: Requirements 5.4, 9.2**

  - [ ] 10.3 Write property test for Property 3 — zero-interest conditions
    - // Feature: time-deposit-api, Property 3: Zero-interest conditions
    - Generate deposits where `days` ≤ 30, or `planType=student` with `days` ≥ 366, or `planType=premium` with `days` ≤ 45; call `updateBalance`; assert balance is unchanged
    - **Validates: Requirements 6.1, 6.4, 6.6**

  - [ ] 10.4 Write property test for Property 4 — basic plan interest calculation
    - // Feature: time-deposit-api, Property 4: Basic plan interest calculation
    - Generate `planType=basic` deposits with `days` > 30; call `updateBalance`; assert new balance equals `balance + round(balance × 0.01 / 12, 2, HALF_UP)`
    - **Validates: Requirements 6.2**

  - [ ] 10.5 Write property test for Property 5 — student plan interest calculation
    - // Feature: time-deposit-api, Property 5: Student plan interest calculation
    - Generate `planType=student` deposits with `30 < days < 366`; call `updateBalance`; assert new balance equals `balance + round(balance × 0.03 / 12, 2, HALF_UP)`
    - **Validates: Requirements 6.3**

  - [ ] 10.6 Write property test for Property 6 — premium plan interest calculation
    - // Feature: time-deposit-api, Property 6: Premium plan interest calculation
    - Generate `planType=premium` deposits with `days` > 45; call `updateBalance`; assert new balance equals `balance + round(balance × 0.05 / 12, 2, HALF_UP)`
    - **Validates: Requirements 6.5**

- [ ] 11. Implement Testcontainers integration tests
  - [x] 11.1 Create base integration test configuration
    - Create abstract `AbstractIntegrationTest` with `@SpringBootTest(webEnvironment=RANDOM_PORT)`, `@Testcontainers`, `@Container PostgreSQLContainer`, and `@DynamicPropertySource` injecting the container JDBC URL
    - _Requirements: 9.1_

  - [x] 11.2 Write integration test for GET /time-deposits
    - Extend `AbstractIntegrationTest`; seed `time_deposits` and `withdrawals` rows directly via JPA; call `GET /time-deposits` with `TestRestTemplate`; assert HTTP 200 and response body matches seeded data
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 9.3_

  - [x] 11.3 Write integration test for POST /time-deposits/update-balances
    - Extend `AbstractIntegrationTest`; seed deposits; call `POST /time-deposits/update-balances`; query DB directly; assert each balance matches `TimeDepositCalculator.updateBalance` output
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 9.2_

- [ ] 12. Final checkpoint — Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- `TimeDeposit` and `TimeDepositCalculator` in `org.ikigaidigital` must not be modified at any point
- Property tests (tasks 10.x) exercise `TimeDepositCalculator` directly — no Spring context needed for Properties 3–6
- Properties 1 and 2 require a running Spring context + Testcontainers PostgreSQL
- jqwik requires explicit engine registration in `maven-surefire-plugin` configuration
