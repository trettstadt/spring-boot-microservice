# Microservice example app using Spring Boot, Maven, OAuth2 / OIDC, REST clients, observability

This example shows a more conservative example for a microservice using Java, Spring Boot, Maven, OAuth2 / OIDC for
security and REST client access to other services or legacy applications.

## Features and technologies

- Java and Spring Boot (see ADRs 0002 and 0003 in `docs/adr`)
- ports and adapters architecture (see ADR 0004)
- quality gates for test coverage, code style and potential bugs
- OAuth2 for security in REST clients and server
- observability via metrics and tracing
- build with Github workflows and deploy to Github packages
