##
# Multi-stage Dockerfile for Marka Bot. The first stage builds the project using
# Gradle, and the second stage creates a minimal image containing only the
# compiled application and a JRE. To reduce attack surface, a slim base image is
# used for the runtime.
#
# Usage:
#
#  docker build -t markabot .
#  docker run -e BOT_TOKEN=... -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... markabot
##

### Build stage
FROM gradle:8.3-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy only the Gradle wrapper and build files first to leverage Docker cache
COPY build.gradle settings.gradle ./
COPY app-core/build.gradle app-core/
COPY infra/build.gradle infra/
COPY discord-adapter/build.gradle discord-adapter/
COPY bootstrap/build.gradle bootstrap/

# Copy the rest of the sources
COPY . .

# Build the distribution for the bootstrap module. Tests are skipped for faster builds.
RUN gradle :bootstrap:installDist -x test --no-daemon

### Runtime stage
FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

# Copy the distribution from the builder stage
COPY --from=builder /home/gradle/project/bootstrap/build/install/bootstrap/ .

# Set the entrypoint. The bash wrapper from installDist sets classpath correctly.
ENTRYPOINT ["./bin/bootstrap"]

# The following environment variables must be provided at runtime:
# - BOT_TOKEN
# - DB_URL
# - DB_USERNAME
# - DB_PASSWORD
# - BOT_VERSION (optional)