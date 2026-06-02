# PROJECT KNOWLEDGE BASE

**Generated:** 2026-05-24
**Commit:** `824127f` (main)

## OVERVIEW

Booking management microservice. Spring Boot 4.0.6 + Java 25 + Ports & Adapters (Hexagonal) architecture. REST API with OAuth2 (Keycloak), PostgreSQL persistence, Liquibase migrations, OpenAPI codegen.

## STRUCTURE

```
./
├── src/main/java/.../springbootmicroservice/
│   ├── adapter/in/rest/         # REST controllers (implements OpenAPI-generated interfaces)
│   ├── adapter/out/persistence/ # JPA entities, repositories, persistence adapters
│   ├── adapter/out/rooms/       # OpenAPI-generated REST client for Rooms service
│   ├── application/domain/      # Pure Java records + business logic (no framework deps)
│   ├── application/port/in/     # Input port interfaces (use cases) + DTOs
│   ├── application/port/out/    # Output port interfaces (contracts for persistence/ext APIs)
│   └── common/config/           # Security config only (WebSecurityConfiguration)
├── src/main/oas/                # OpenAPI specs (booking/, rooms/)
├── gitops/booking/              # Helm chart for K8s deployment (ArgoCD/Flux)
├── localdev/                    # Docker Compose stack (PostgreSQL, Keycloak, OTEL)
└── docs/adr/                    # Architecture Decision Records (4 ADRs)
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add new endpoint | `adapter/in/rest/` + OpenAPI spec in `src/main/oas/booking/` | Controller implements generated `*Api` interface |
| Add business logic | `application/domain/service/` + `application/port/in/` | Create `*UseCase` interface + implementation |
| Add persistence | `adapter/out/persistence/` + `application/port/out/booking/` | Entity, Repository, Adapter, Mapper |
| Add external API client | `adapter/out/rooms/` + `application/port/out/rooms/` | Follow RoomsClient pattern (OAuth2 interceptor) |
| Add DB migration | `src/main/resources/db/changelog/` | Liquibase YAML changelogs |
| Modify security | `common/config/WebSecurityConfiguration.java` | Only config class in project |
| Write unit test | Mirror main in `src/test/java/` | Use `@ExtendWith(MockitoExtension.class)` |
| Write integration test | Same test dir, add `@Tag("integration")` | Run via failsafe, NOT surefire |

## CODE MAP

| Symbol | Type | Location | Role |
|--------|------|----------|------|
| `SpringBootMicroserviceApplication` | Class | Root | `@SpringBootApplication` entry point |
| `ListBookingsUseCase` | Interface | `port/in/` | Input port (use case contract) |
| `BookingInPort` | Record | `port/in/` | Input port DTO |
| `BookingDomain` | Record | `domain/model/` | Pure domain model |
| `BookingService` | Class | `domain/service/` | `@Service` — implements `ListBookingsUseCase` |
| `FindBookings` | Interface | `port/out/booking/` | Output port (persistence contract) |
| `BookingOutPort` | Record | `port/out/booking/` | Output port DTO |
| `BookingPersistenceAdapter` | Class | `out/persistence/` | `@Service` — implements `FindBookings` |
| `BookingEntryEntity` | Class | `out/persistence/` | JPA `@Entity` |
| `BookingController` | Class | `in/rest/` | `@RestController` — implements `BookingsApi` |
| `WebSecurityConfiguration` | Class | `common/config/` | `@Configuration` + `@EnableWebSecurity` |
| `FindRooms` | Interface | `port/out/rooms/` | Output port (Rooms API contract) |
| `FindRoomsAdapter` | Class | `out/rooms/` | `@Service` — implements `FindRooms` |
| `RoomsClient` | Class | `out/rooms/` | `@Component` — OAuth2 RestClient wrapper |

## CONVENTIONS

- **Every boundary crossing needs a MapStruct mapper**: API↔InPort, Domain↔OutPort, Entity↔OutPort. 4 mappers per flow.
- **MapStruct style**: `componentModel = "spring"`, `injectionStrategy = InjectionStrategy.CONSTRUCTOR`
- **Domain objects are Java records**: `BookingDomain`, `BookingInPort`, `BookingOutPort` — pure data carriers
- **JPA entities use Lombok**: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`, suffixed `*Entity`
- **Output port interfaces are verb-named**: `FindBookings` (not `BookingRepository`)
- **Output port classes are `*Adapter` or `*Client`**: `BookingPersistenceAdapter`, `RoomsClient`
- **Integration tests tagged**: `@Tag("integration")` — separated by surefire/failsafe
- **AssertJ only**: No Hamcrest, no JUnit assertions
- **`@Service` on adapters AND domain services**: Both `BookingPersistenceAdapter` and `BookingService` use `@Service`
- **Jackson 3**: `tools.jackson.core` (not `com.fasterxml.jackson`)
- **JSpecify null-safety**: `org.jspecify.annotations.Nullable` (not `javax.annotation`)

## ANTI-PATTERNS (THIS PROJECT)

- `main()` is package-private (`static void` not `public static void`) — works but unconventional
- `RoomsConfigurationProperties` (`@Component` + `@ConfigurationProperties`) lives in `application/port/out/rooms/` — should be in adapter layer (port layer must be framework-free)
- Liquibase properties path in POM is wrong: `<propertyFile>liquibase.properties</propertyFile>` should be `src/main/resources/liquibase.properties`
- `javax.annotation-api` dependency included unnecessarily (jakarta is the standard)
- CI skips tests (`-DskipTests`) and uses system `mvn` not `./mvnw`
- GitOps image tag in Helm values is hardcoded SHA — no automated update pipeline

## UNIQUE STYLES

- **OpenAPI → Hexagonal flow**: OpenAPI spec → codegen `*Api` interface → `@RestController implements *Api` → `*UseCase` interface → `@Service implements *UseCase` → output port interface → adapter
- **No `@InjectMocks`**: Manual constructor injection in `@BeforeEach setUp()` even in unit tests
- **WireMock via `@RegisterExtension`** (not `@WireMockTest`): Static field with dynamic port
- **`@MockitoBean`**: Spring Boot 4 replacement for `@MockBean` in integration tests
- **`@PreAuthorize` per method**: `hasAuthority('SCOPE_bookings')` on controller methods, not class-level
- **Liquibase changelogs**: Single `db.changelog-master.yaml` with `booking_entry` table

## COMMANDS

```bash
./mvnw verify          # Full build: tests + checkstyle + pmd + spotbugs + jacoco (60%)
./mvnw package -DskipTests  # Quick build (see ANTI-PATTERNS — CI also skips tests)
./mvnw spring-boot:run # Run with localdev profile (needs docker-compose up)
docker compose -f localdev/docker-compose.yaml up -d  # PostgreSQL + Keycloak + OTEL
./mvnw clean test      # Unit tests only (excludes @Tag("integration"))
./mvnw clean verify -Dit.test="*"  # Integration tests only
```

## NOTES

- **No `.editorconfig`**: Configure IDE manually for Google Java style (2-space indent)
- **No `lombok.config`**: Lombok runs with defaults — MapStruct integration is via explicit constructor strategy
- **Generated code excluded**: OpenAPI-generated sources in `adapter/*/api/` excluded from JaCoCo, PMD, Checkstyle, SpotBugs
- **JaCoCo enforces 60% complexity**: Merged unit + integration coverage; excludes generated code and main class
- **CVE overrides** in POM: Tomcat, Jackson, Bouncy Castle — check regularly for updates
- **GraalVM native build** supported via `native-maven-plugin` but not CI-tested
