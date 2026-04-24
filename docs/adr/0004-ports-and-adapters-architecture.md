# 4. Use Ports and Adapters (Hexagonal) Architecture

Date: 2026-04-19

## Status

Accepted

## Context

We need to define the overall architectural style for this Spring Boot microservice to ensure:

- Clear separation between business logic and external concerns (databases, APIs, messaging)
- Testability at different layers (unit vs integration)
- Flexibility to swap implementations without touching core domain logic
- Long-term maintainability as the service grows in complexity

## Decision

The service will use **Ports and Adapters (Hexagonal) Architecture**.

### Key Principles

1. **Domain is the core** - No dependencies on frameworks or infrastructure
2. **Ports define contracts** - Interfaces in `application/ports/` that adapters implement
3. **Adapters are pluggable** - Implement `in` and `out` ports; swap implementations (e.g., JPA → MongoDB)
4. **Controllers use input ports** - Handle HTTP, map to use cases via input ports
5. **Dependency direction inward** - Outer layers depend on inner, never vice versa

## Consequences

Ports and Adapters Architecture brings clear separation between business logic and infrastructure concerns. Testability
improves by allowing unit tests of domain logic without Spring or database dependencies. Flexibility to swap persistence
implementations (JPA → MongoDB) without changing domain code. The trade-off is additional indirection and slightly more
initial structure compared to a simple layered architecture.
