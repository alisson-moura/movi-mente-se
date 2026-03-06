# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start PostgreSQL (required before running app or tests)
docker compose up -d

# Run application
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=CadastrarAlunoE2ETest

# Build
mvn clean package
```

## Architecture

Spring Boot 4.0.2 REST API using raw JDBC (no JPA/Hibernate). Java 25.

**Layer structure:** `controller/` → `entity/` → `repository/` → PostgreSQL

- **No service layer** — business logic lives in controllers and entity classes
- **DTOs** are Java records in `dto/`
- **Exceptions** are mapped to HTTP responses via `GlobalExceptionHandler`

## Database

PostgreSQL managed via Flyway migrations in `src/main/resources/db/migration/`. Migration files follow `V{n}__{description}.sql` naming.

Docker Compose (`compose.yaml`) runs PostgreSQL 16 on port 5432. Both dev and test profiles connect to the same database instance (`movimente-se`).

Credentials (dev/test): `admin` / `admin@123`

## Authentication

Session-based auth using a 96-char hex token stored in an HTTP-only `session_id` cookie. Sessions expire in 30 days. Passwords are hashed with BCrypt (cost 12) via the `Senha` entity class.

## Testing

All tests are E2E tests (no unit tests). They hit the real PostgreSQL database. `BaseE2ETest` truncates relevant tables before each test and provides a mocked `Clock` for time-controlled scenarios. Test data is built with `AlunoTestDataBuilder` using `datafaker` with the `pt-BR` locale.
