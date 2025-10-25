# Stage 1: Build the JAR
FROM maven:3.9-eclipse-temurin-21-jammy AS builder

WORKDIR /build
COPY pom.xml .
COPY src src

# Build the JAR
RUN mvn clean package

# Stage 2: Create the Trino image with the plugin
FROM trinodb/trino:476

# Create plugin directory
RUN mkdir -p /usr/lib/trino/plugin/http-group-provider/

# Copy the JAR from builder stage
COPY --from=builder /build/target/trino-http-group-provider-*.jar /usr/lib/trino/plugin/http-group-provider/
