# 3. Use Spring Boot as application framework

Date: 2026-04-19

## Status

Accepted

## Context

We need to choose an application framework that accelerates development while providing production-ready features.
The microservice requires OAuth2/OIDC security, REST client capabilities, and standard enterprise integrations.

## Decision

The service will be built on Spring Boot. Spring Boot provides:
- Embedded server (Tomcat) with production-grade configuration
- Auto-configuration for common dependencies
- Spring Security with OAuth2/OIDC support out of the box
- RestClient and WebClient for HTTP communications
- actuator for observability and health endpoints

## Consequences

Spring Boot brings a significant dependency footprint. The framework's convention-over-configuration approach
may limit flexibility for unconventional requirements. However, the mature ecosystem and extensive documentation
offset these trade-offs for enterprise microservice development.