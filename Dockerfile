ARG VERSION

# Stage 1: Build the JAR
FROM maven:3.9-amazoncorretto-24-alpine AS builder

ARG VERSION

WORKDIR /build
COPY pom.xml .
COPY src src
COPY config config

# Build the JAR
RUN [[ "${VERSION}" = "$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)" ]] || exit 1 \
    && mvn clean package

# Stage 2: Create the Trino image with the plugin
FROM trinodb/trino:479

ARG VERSION

# Create plugin directory
RUN mkdir -p /usr/lib/trino/plugin/http-group-provider/

# Copy the JAR from builder stage
COPY --from=builder /build/target/trino-http-group-provider-${VERSION}/*.jar /usr/lib/trino/plugin/http-group-provider
