# ─────────────────────────────────────────────────────────────────
# Stage 1 – Build
#   Uses Eclipse Temurin 21 (LTS) + Maven to compile the project
# ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper + pom first for layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
# If you use the Maven wrapper instead of system Maven, copy it:
# COPY mvnw .

# Download dependencies (cached unless pom.xml changes)
RUN apt-get update && apt-get install -y maven --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────────────────
# Stage 2 – Runtime
#   Slim JRE image; only the compiled JAR is copied over
# ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy AS runtime

LABEL maintainer="your-email@example.com"
LABEL description="Spring Boot 3.4.5 CRUD API with MongoDB"
LABEL java.version="21"

# Create non-root user for security
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Spring Boot default port
EXPOSE 8080

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
