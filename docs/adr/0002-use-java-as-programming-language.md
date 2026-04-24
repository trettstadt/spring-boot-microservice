# 2. Use Java as Programming Language

Date: 2026-04-19

Status: Accepted

## Context

We need to build a stable and reliable microservice with a strong focus on maintainability, long-term supportability, and team productivity. Key requirements include:

- **Hiring**: Need access to a large pool of experienced developers
- **Learning curve**: New team members should be productive quickly
- **Tooling**: Mature IDE support, debugging, and profiling tools
- **Ecosystem**: Extensive libraries for enterprise integration, security, and cloud-native development
- **Stability**: Prefer battle-tested technology over cutting-edge trends

## Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| **Kotlin** | More concise, null-safe, modern features | Smaller hiring pool, steeper learning curve for Java devs, smaller ecosystem |
| **Scala** | Functional features, Akka ecosystem | Complex syntax, slower compilation, smaller hiring pool |
| **Python** | Fast development, rich libraries | Performance limitations, no type safety, harder to maintain at scale |
| **Go** | High performance, simple, good concurrency | Less enterprise library support, no generics (historically), different paradigm |
| **Node.js/TypeScript** | Full-stack flexibility, async I/O | Untyped by default (even with TS), callback hell risk, different runtime model |

## Decision

The service will be implemented in **Java 25 LTS**. This version provides:

- **Virtual Threads** (Project Loom) for high-throughput concurrency
- **Pattern Matching** for cleaner code
- **Records** for immutable data classes
- **Switch Expressions** for more readable control flow
- **ClassFile API** for smoother bytecode processing
- **Structured Concurrency** for better thread management
- **Long-term support** through 2032+

While Kotlin offers modern language features and is fully interoperable with Java, we chose Java because:

1. Larger talent pool - easier to hire and onboard
2. Mature ecosystem - Spring Boot has best-in-class Java support
3. Verbosity = clarity - explicit code reduces cognitive load
4. Interoperability - can call Kotlin from Java if needed later

## Consequences

### Positive

- **Large hiring pool**: Java remains one of the most popular programming languages globally
- **Mature tooling**: IntelliJ IDEA, Eclipse, VS Code have excellent Java support
- **Rich ecosystem**: Spring Boot, libraries, frameworks for every use case
- **Strong type safety**: Compile-time error detection reduces runtime bugs
- **Excellent profiling**: VisualVM, JProfiler, async-profiler for performance debugging
- **Enterprise support**: Long-term support from Oracle, Red Hat, and community

### Negative

- **Higher memory footprint**: JVM consumes more memory than Go or Node.js (mitigate with proper JVM tuning)
- **Verbose code**: More boilerplate compared to Kotlin or Scala
- **Cold start times**: Longer startup for serverless/container scenarios (mitigate with GraalVM native images if needed)
- **Release cadence**: New features arrive slower than languages with more frequent releases

## Recommendations

1. **Coding Standards**: Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
2. **Build Tool**: Use Maven (consistent with Spring Boot ecosystem)
3. **Minimum Java Version**: Require Java 25 for development; target Java 25 for compilation
4. **Lombok**: Consider using Lombok to reduce boilerplate (records preferred for new code)
5. **Testing**: JUnit 5 for unit tests, Testcontainers for integration tests
6. **IDE Configuration**: Use consistent code formatting across team (e.g., .editorconfig + formatter plugin) 
