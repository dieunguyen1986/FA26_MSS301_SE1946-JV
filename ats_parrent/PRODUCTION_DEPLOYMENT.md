# ATS Parent Production Deployment Guide

This guide explains how to deploy the ATS Parent services with the Spring Cloud Config Server in production.

## 1. Production architecture

Production uses two separate repositories:

1. **Application repository**: this repository, containing Java source code and the Config Server.
2. **Private configuration repository**: a separate Git repository containing `application.yml`, service configuration files, and environment-variable placeholders.

The Config Server clones the private configuration repository at startup. Application services request their configuration from the Config Server before they start.

The production startup flow is:

```text
Private config repository
          |
          v
    Config Server  --->  Eureka Server  --->  Application services  --->  API Gateway
```

## 2. Prepare the private configuration repository

Create a private Git repository and copy these files from the local `config-repo/` directory:

```text
application.yml
job-service.yml
candidate-service.yml
application-service.yml
api-gateway.yml
eureka-server.yml
```

Keep secrets as placeholders. For example:

```yaml
spring:
  datasource:
    url: ${JOB_DB_URL}
    username: ${JOB_DB_USERNAME}
    password: ${JOB_DB_PASSWORD}
```

Do not commit:

- Database passwords
- Git tokens
- Config Server passwords
- OAuth client secrets
- Private keys

Use a secret manager, Kubernetes Secret, Docker secret, or deployment environment variables for these values.

## 3. Config Server environment variables

Set these variables on the Config Server host/container:

```env
# HTTP port used by the Config Server.
CONFIG_SERVER_PORT=8888

# IMPORTANT: production uses the Git backend, not the local native directory.
CONFIG_SERVER_PROFILE=git

# Basic Auth credentials required by application services.
CONFIG_SERVER_USERNAME=config-admin
CONFIG_SERVER_PASSWORD=<strong-unique-password>

# Private configuration repository.
CONFIG_REPO_URI=https://git.example.com/company/ats-config.git
CONFIG_REPO_BRANCH=main
CONFIG_REPO_USERNAME=git-readonly-user
CONFIG_REPO_PASSWORD=<git-access-token>
```

`CONFIG_REPO_PASSWORD` should normally be a read-only repository token. Never place these values directly in `application.yml` or commit them to Git.

## 4. Application service environment variables

Every service must know how to reach the Config Server:

```env
CONFIG_SERVER_URL=https://config.example.com
CONFIG_SERVER_USERNAME=config-admin
CONFIG_SERVER_PASSWORD=<same-config-server-password>
SPRING_PROFILES_ACTIVE=prod
```

Each database should have its own user and password:

```env
JOB_DB_URL=jdbc:postgresql://postgres.example.com:5432/job_sdb
JOB_DB_USERNAME=job_service
JOB_DB_PASSWORD=<job-db-password>

CANDIDATE_DB_URL=jdbc:postgresql://postgres.example.com:5432/candidate_sdb
CANDIDATE_DB_USERNAME=candidate_service
CANDIDATE_DB_PASSWORD=<candidate-db-password>

APPLICATION_DB_URL=jdbc:postgresql://postgres.example.com:5432/application_sdb
APPLICATION_DB_USERNAME=application_service
APPLICATION_DB_PASSWORD=<application-db-password>
```

The API Gateway additionally needs its OAuth/OIDC settings:

```env
APP_CLIENT_ID=<oauth-client-id>
APP_OIDC_ISSUER=https://accounts.google.com
APP_OIDC_JWK_SET_URI=https://www.googleapis.com/oauth2/v3/certs
APP_CORS_ALLOWED_ORIGINS=https://app.example.com
```

Use the production frontend domain in `APP_CORS_ALLOWED_ORIGINS`. Do not use `*` together with credentials.

## 5. Build production artifacts

Build from the application repository:

```bash
mvn clean package -DskipTests
```

The build creates executable Spring Boot JARs. Do not copy the local `config-repo/` directory into the Config Server JAR; production configuration comes from `CONFIG_REPO_URI`.

Before deployment, run the test suite as part of CI:

```bash
mvn clean verify
```

## 6. Deployment order

Start components in this order:

1. Config Server
2. Eureka Server
3. `job-service`
4. `candidate-service`
5. `application-service`
6. API Gateway

The Config Server must be reachable before the other Spring applications start because `fail-fast` is enabled for Config Client applications.

## 7. Production database safety

The `prod` profile uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

This prevents Hibernate from changing the production schema automatically. Run database migrations separately before deploying a new application version.

## 8. Security and networking checklist

- Put the Config Server behind HTTPS.
- Allow only application services to access the Config Server network endpoint.
- Do not expose the Config Server publicly without authentication and network restrictions.
- Expose only `health` and `info` actuator endpoints.
- Use a read-only Git token for the configuration repository.
- Rotate Config Server, database, OAuth, and Git credentials regularly.
- Add secrets to `.gitignore` and verify that CI does not print them in logs.
- Use separate credentials and configuration repositories for each environment.

## 9. Health checks

The Config Server health endpoint is:

```text
https://config.example.com/actuator/health
```

An authenticated configuration request should return HTTP `200`:

```bash
curl -u "$CONFIG_SERVER_USERNAME:$CONFIG_SERVER_PASSWORD" \
  https://config.example.com/job-service/prod
```

An unauthenticated configuration request should return HTTP `401`.

## 10. Release and rollback

For a release:

1. Create a Git commit/tag in the private configuration repository.
2. Deploy the application artifacts built by CI.
3. Set `CONFIG_REPO_BRANCH` to the approved branch or release tag.
4. Start the Config Server and verify its health.
5. Start the services and verify service registration in Eureka.
6. Verify the API Gateway routes and authentication.

For rollback, deploy the previous application artifacts and point `CONFIG_REPO_BRANCH` to the previous known-good configuration tag.
