# Build one selected Maven module and its reactor dependencies.
FROM maven:3.9-eclipse-temurin-21 AS build

# Compose provides the module path and final artifact name per service.
ARG SERVICE_MODULE
ARG SERVICE_ARTIFACT

WORKDIR /workspace
# The root pom is required to resolve the multi-module build.
COPY . .

# Tests run separately. Image creation only packages the service.
RUN mvn -pl "${SERVICE_MODULE}" -am package -DskipTests

# Keep the runtime image limited to the Java runtime.
FROM eclipse-temurin:21-jre

ARG SERVICE_MODULE
ARG SERVICE_ARTIFACT

WORKDIR /app
# Copy the executable Spring Boot jar from the build stage.
COPY --from=build "/workspace/${SERVICE_MODULE}/target/${SERVICE_ARTIFACT}-1.0-SNAPSHOT.jar" app.jar

# Start the selected service.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
