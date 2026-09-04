# syntax=docker/dockerfile:1

# ---- Build stage -----------------------------------------------------------
# Build the Spring Boot fat jar with Maven on a JDK 17 image. This stage uses
# public Maven Central directly, so the project-local .mvn/settings.xml (which
# only exists to bypass a corporate mirror on developer machines) is not used.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy the full project and build. Tests are skipped in the image build; they
# run in CI. Integration tests need Docker (Testcontainers), unavailable here.
# A BuildKit cache mount persists the Maven repository across builds, so
# dependencies are not re-downloaded every time even though there is no
# separate go-offline step.
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q clean package -DskipTests

# ---- Runtime stage ---------------------------------------------------------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Run as a non-root user.
RUN groupadd --system app && useradd --system --gid app app

COPY --from=build /workspace/target/todo-0.0.1-SNAPSHOT.jar app.jar
RUN chown -R app:app /app
USER app

EXPOSE 8080

# Database connection and CORS are supplied via environment variables at
# runtime (see application.yml): DB_HOST, DB_PORT, DB_NAME, DB_USERNAME,
# DB_PASSWORD, CORS_ALLOWED_ORIGINS.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
