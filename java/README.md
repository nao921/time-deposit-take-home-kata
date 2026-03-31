# Time Deposit Refactoring Kata - Java

## Recommended setup
- Java 21+
- Maven 3.8+
- Docker 29+ (for starting a local Postgres instance and Testcontainers during tests)

## Quickstart
### 1. Run test
```bash
mvn clean test
```
### 2. Start the app locally
Start Postgres with Docker Compose:
```bash
docker compose up
```
Then run the Spring Boot app:
```bash
mvn spring-boot:run
```

### 3. Once finished, clean up
```bash
docker compose down -v
```

## API Endpoints 
| Method | Path | Description |
|--------|------|-------------|
| GET | `/time-deposits` | List all deposits with withdrawals |
| POST | `/time-deposits/update-balances` | Recalculate and update all balances |

OpenAPI spec: `src/main/resources/openapi.yaml`

## Interacting with the API
### With Swagger UI
After starting the app, you can navigate to Swagger UI at `http://localhost:8080/swagger-ui.html`.
You can go to each endpoint, click "Try it out", and execute the request to see the response.

### With curl
#### List  all deposits with withdrawals:
```bash
curl -X GET "http://localhost:8080/time-deposits" -H "accept: application/json"
```

#### Recalculate and update all balances:
```bash
curl -X POST "http://localhost:8080/time-deposits/update-balances" -H "accept: application/json"
```

## Database Migration with Flyway
The project uses Flyway for database migrations. Migration scripts are located in `src/main/resources/db/migration`. 
When the application starts, Flyway will automatically apply any pending migrations to the database during the startup process.

The following initial time deposits records are populated into the database for manual testing convenience.
```sql
refer to src/main/resources/db/migration/V2__Adding_sample_data.sql
```