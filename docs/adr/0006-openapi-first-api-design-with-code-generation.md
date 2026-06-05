# 6. OpenAPI-First API Design with Code Generation

Date: 2026-06-02

## Status

Accepted

## Context

The microservice exposes a REST API and also consumes a third-party REST API (Rooms service). We needed an approach to design, document, and implement these APIs consistently.

The main options were:

- **Hand-written controllers and DTOs**: Fast to start, but the API contract exists only implicitly in the code. No machine-readable specification, no automatic documentation, no client generation. Contract drift between service and consumers is common.

- **Spring REST Docs**: Generates documentation from annotated tests. Produces accurate documentation but requires maintaining test fixtures for every endpoint. Still no machine-readable spec for client code generation.

- **OpenAPI-first with code generation**: The API contract is defined as an OpenAPI specification file first. Server interfaces and DTOs are generated from it, and the controller implements the generated interface. For outbound calls, a client is generated from the upstream service's OpenAPI spec.

The project goal is a production-hardened template. Long-term maintainability and clear contracts between services are essential — especially in a microservice ecosystem where multiple teams own different services.

## Decision

We will use an **OpenAPI-first approach with code generation** for both inbound REST APIs and outbound REST clients.

### Inbound (our API)

1. The API contract is defined in `src/main/oas/booking/openapi.yaml`.
2. During the Maven build, the `openapi-generator-maven-plugin` generates a `*Api` interface and request/response DTOs from the spec.
3. A `@RestController` implements the generated `*Api` interface, delegating to use case interfaces in the application layer.
4. MapStruct mappers convert between the generated DTOs and the application's input port DTOs (see ADR 0009).

### Outbound (external APIs we call)

1. The upstream service's OpenAPI spec is stored at `src/main/oas/rooms/openapi.yaml`.
2. Code generation produces a `*Client` class with request DTOs and a RestClient-based HTTP client.
3. An adapter (`FindRoomsAdapter`) wraps the generated client and implements the application's output port interface (`FindRooms`).
4. The generated client is configured with OAuth2 client credentials via a Spring `RestClient` interceptor.

### Spec management

- OpenAPI specs are hand-authored YAML files checked into version control. They are the source of truth for the API contract.
- The `openapi-generator-maven-plugin` is configured in the build `pom.xml` with two executions — one for the booking API (server stub), one for the rooms API (client).
- Generated sources are placed in `target/generated-sources/` and excluded from static analysis (JaCoCo, PMD, Checkstyle, SpotBugs) via POM exclusions.

### Flow

```
OpenAPI spec (booking/openapi.yaml)
  → codegen generates BookingsApi interface + DTOs
  → BookingController implements BookingsApi
  → delegates to BookingService (via ListBookingsUseCase)
  → returns mapped response

OpenAPI spec (rooms/openapi.yaml)
  → codegen generates RoomsClient + DTOs
  → FindRoomsAdapter wraps RoomsClient
  → implements FindRooms output port
  → called by BookingService
```

## Consequences

### Positive

- **Contract first**: The API is designed and reviewed at the specification level before implementation begins. This catches design issues early and provides a clear target for implementers.
- **Generated boilerplate**: DTO classes, serialization annotations, and interface methods are generated — reducing manual coding errors and ensuring the implementation always matches the spec.
- **Documentation is free**: The OpenAPI spec serves as both the contract and the documentation. It can be served via Swagger UI or SpringDoc.
- **Cross-service consistency**: Both inbound and outbound APIs use the same generation pipeline. The Rooms client is generated from the same kind of spec that we publish for our own API.
- **Version-controlled contract**: Spec changes are visible in code review, with clear diff context. Breaking changes are obvious at the spec level.
- **Generated code excluded from quality gates**: The build is configured to exclude generated sources from JaCoCo coverage requirements, PMD, Checkstyle, and SpotBugs — preventing false positives from code that should not be hand-modified.

### Negative

- **Spec maintenance**: The OpenAPI spec must be kept in sync with actual behavior. If a controller deviates from the spec, the generated interface will fail to compile — making this a compile-time check rather than a runtime drift risk.
- **Generated code verbosity**: The generated DTOs add a layer of indirection. Combined with MapStruct mappers (ADR 0009), a single endpoint involves more files than a hand-written approach. This is an accepted cost for the contract guarantee.
- **Codegen configuration complexity**: The `openapi-generator-maven-plugin` requires careful configuration for naming conventions, library selection (RestClient vs WebClient vs RestTemplate), and import mappings. Initial setup is non-trivial.
- **Spec-first requires discipline**: It is tempting to add a quick endpoint by writing the controller directly. Enforcing the spec-first workflow requires team discipline and build-time checks.
