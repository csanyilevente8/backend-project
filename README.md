# Todo Backend (Spring Boot)

REST API for the full-stack Todo application. Java 17, Spring Boot 3.x,
Spring Data JPA, Flyway and PostgreSQL.

## Architecture

```
HTTP Request
     |
Controller  (com.example.todo.controller)   -> HTTP concerns only
     |
Service     (com.example.todo.service)       -> business logic
     |
Repository  (com.example.todo.repository)    -> persistence
     |
PostgreSQL
```

JPA entities are never exposed over HTTP; the API uses DTOs
(`com.example.todo.dto`). Errors are handled centrally by a
`@RestControllerAdvice` (`com.example.todo.exception.GlobalExceptionHandler`).

## Prerequisites

- Java 17 (an LTS JDK; the project is compiled and tested with Java 17)
- Maven 3.9+
- Docker (for the local PostgreSQL started from the workspace root, and for
  the Testcontainers-based integration tests)

## Database

The application connects to PostgreSQL. Configuration is driven by environment
variables (see below). The schema is created and versioned by **Flyway**
(`src/main/resources/db/migration`), and Hibernate is set to
`ddl-auto=validate`, so it validates the schema but never creates or alters it.

Start PostgreSQL from the **workspace root** (one level up):

```bash
cd ..
docker compose up -d
```

## Environment variables

| Variable                | Default                 | Description                       |
|-------------------------|-------------------------|-----------------------------------|
| `DB_HOST`               | `localhost`             | PostgreSQL host                   |
| `DB_PORT`               | `5432`                  | PostgreSQL port                   |
| `DB_NAME`               | `todo`                  | Database name                     |
| `DB_USERNAME`           | `todo`                  | Database user                     |
| `DB_PASSWORD`           | `todo`                  | Database password                 |
| `CORS_ALLOWED_ORIGINS`  | `http://localhost:4200` | Comma-separated allowed origins   |

No secrets are committed. For production, supply these through GitHub Secrets,
Google Cloud Secret Manager or Kubernetes Secrets.

## Run the backend

```bash
mvn spring-boot:run
```

or run the packaged jar:

```bash
mvn package
java -jar target/todo-0.0.1-SNAPSHOT.jar
```

The application listens on `http://localhost:8080`.

## Run tests

```bash
mvn test
```

The suite includes unit tests (Mockito), web-layer tests (`@WebMvcTest`) and a
full integration test using Testcontainers + PostgreSQL. The build fails if any
test fails.

## Build

```bash
mvn package
```

## API

Base path: `/api/todos`

| Method   | Path                        | Description             | Success |
|----------|-----------------------------|-------------------------|---------|
| `POST`   | `/api/todos`                | Create a todo           | 201     |
| `GET`    | `/api/todos`                | List all todos          | 200     |
| `GET`    | `/api/todos/{id}`           | Get one todo            | 200/404 |
| `PUT`    | `/api/todos/{id}`           | Update a todo           | 200/404 |
| `PATCH`  | `/api/todos/{id}/complete`  | Update completion only  | 200/404 |
| `DELETE` | `/api/todos/{id}`           | Delete a todo           | 204/404 |

### Validation

- `title`: required, 1–255 characters
- `description`: optional, up to 2000 characters

Invalid requests return `400` with a structured error body:

```json
{
  "timestamp": "2026-09-03T12:00:00Z",
  "status": 400,
  "error": "Validation failed",
  "message": "Invalid request",
  "fieldErrors": { "title": "Title must not be empty" }
}
```

## Swagger / OpenAPI

With the application running:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Health check

- http://localhost:8080/actuator/health

Returns `{"status":"UP"}` and is suitable for Kubernetes/GKE probes later.

## GitHub Actions

CI is defined in `.github/workflows/backend-ci.yml`. It runs on pushes to the
`main` branch and on pull requests targeting `main`: it sets up JDK 17, runs
`mvn test` (including the Testcontainers integration test, using the Docker
preinstalled on the runner) and then `mvn package`. The build fails if tests
fail.

## Local Maven note

A machine-specific `~/.m2/settings.xml` may route Maven through a private
mirror. For local builds against Maven Central without touching that global
config, a project-local settings file is provided:

```bash
mvn -s .mvn/settings.xml test
```

CI uses the default Maven Central and does not need this file.

this line will trigger the pipeline
