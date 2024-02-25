# Architectural Decisions

## ADR-1: spring-boot

### Context

Using a general framework to provide libraries or services makes development faster and more reliable.

### Decision

spring-boot is used as basis for all service components.
Quarkus has been used before, but I needed to get current experience with spring-boot and stay up-to-date.

### Consequences

spring-boot has a big influence of lots of cross-cutting-concerns like logging, metrics, security.