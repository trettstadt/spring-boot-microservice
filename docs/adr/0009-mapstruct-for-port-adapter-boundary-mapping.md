# 9. MapStruct for Port-Adapter Boundary Mapping

Date: 2026-06-02

## Status

Accepted

## Context

The Ports and Adapters (Hexagonal) architecture (ADR 0004) mandates strict layer separation. Every boundary crossing between layers requires data conversion:

| Boundary | Source Type | Target Type |
|----------|-------------|-------------|
| REST controller → use case (inbound) | OpenAPI-generated DTO | Input port DTO (`BookingInPort`) |
| Use case → REST response (inbound) | Output port DTO / domain | OpenAPI-generated response DTO |
| Domain service → persistence (outbound) | Domain record (`BookingDomain`) | Output port DTO (`BookingOutPort`) |
| Persistence adapter → domain (outbound) | JPA entity (`BookingEntryEntity`) | Output port DTO |
| Persistence adapter → entity (outbound) | Output port DTO | JPA entity |

This creates a significant amount of mapping code — each field copy, type conversion, and null handling must be implemented. Writing these by hand is tedious, error-prone, and obscures the actual architecture with boilerplate.

The options considered:

- **Manual mapping**: Write `toX()` and `fromX()` methods by hand. Total control, but extremely verbose. Every new field requires updating multiple mappers. Null handling is easy to forget.

- **ModelMapper / Dozer**: Reflection-based mapping that auto-matches fields by name. Reduces boilerplate but introduces runtime reflection, no compile-time safety, and mysterious failures on property mismatches.

- **MapStruct**: Annotation-based code generation at compile time. Generates type-safe mapper implementations with explicit field mappings, null handling, and automatic collection mapping. Failures surface as compilation errors, not runtime bugs.

- **Record mapping via constructors**: Java records have canonical constructors that could be used for mapping. However, this only works for simple copies and cannot handle different field names, type conversions, or complex nested structures.

## Decision

We will use **MapStruct** as the mapping framework for all port-adapter boundary crossings, configured with `componentModel = "spring"` and `injectionStrategy = InjectionStrategy.CONSTRUCTOR`.

### Convention

Every boundary crossing gets a dedicated MapStruct mapper interface:

```
Inbound:  BookingDto → BookingInPort          (BookingControllerMapper)
Domain:   BookingDomain → BookingOutPort      (BookingDomainMapper)
Outbound: BookingOutPort → BookingEntryEntity  (BookingPersistenceMapper)
          BookingEntryEntity → BookingOutPort  (BookingPersistenceMapper)
```

Each mapper is an interface annotated with `@Mapper(config = ...)`, using a shared `CentralMapperConfig` that defines:
- `componentModel = "spring"` — mappers are registered as Spring beans and injectable via constructor.
- `injectionStrategy = InjectionStrategy.CONSTRUCTOR` — consistent with the project's constructor injection convention.
- Default mapping policy for unmapped targets (set to `ERROR` to fail the build on unmapped fields).

### Why constructor injection

MapStruct supports both field injection (`@Autowired`) and constructor injection for its Spring bean integration. We use `InjectionStrategy.CONSTRUCTOR` to:
- Make mapper dependencies explicit and testable.
- Support `final` fields (immutability).
- Stay consistent with the rest of the codebase, which avoids field injection.

### Why not `@InjectMocks`

The test convention in this project is manual constructor injection in `@BeforeEach setUp()` methods rather than `@InjectMocks`. This gives explicit control over mock wiring and avoids Mockito's silent injection failures. It also ensures that adding a new dependency to a class causes a compile error in tests (because the constructor signature changes), rather than a mysterious `NullPointerException` at runtime.

## Consequences

### Positive

- **Compile-time safety**: A field added to a DTO or entity must be explicitly mapped or declared as ignored. Missing mappings are compilation errors, not runtime data corruption.
- **Zero runtime overhead**: MapStruct generates plain Java method calls at compile time. No reflection, no proxies, no runtime classpath scanning.
- **Consistent pattern**: Every boundary crossing follows the same mapper pattern. New developers quickly learn to look for the mapper interface when adding a new field or endpoint.
- **Explicit mapping control**: When field names differ between layers (which is common — OpenAPI DTOs use snake_case, domain records use camelCase), MapStruct's `@Mapping` annotations make the relationship explicit and reviewable.
- **Spring integration**: Mappers are injected like any other Spring bean, consistent with the project's DI style.

### Negative

- **Additional files**: Each mapper interface plus the central config adds files to the project. A simple CRUD endpoint requires 4 mapper interfaces, which feels disproportionate for small changes.
- **Annotation processor overhead**: MapStruct is an annotation processor that runs during compilation. Full clean builds are slightly slower, though incremental compilation mitigates this.
- **Learning curve**: MapStruct's expression language (`@Mapping(target = "date", expression = "java(...)")`) and qualification mechanism for ambiguous mapping methods take time to learn.
- **Occasional complexity**: Maps with deeply nested or conditionally mapped structures can be difficult to express declaratively and may require default methods or custom type converters.
