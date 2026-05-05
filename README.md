# cpp-platform-libraries

`uk.gov.moj.platform.libraries:platform-libraries-parent-pom`

Shared internal libraries used by all CPP bounded-context services. This project provides cross-cutting platform concerns — access control, auditing, feature flags, metrics, search, health checks — as versioned, reusable library JARs consumed by the `cpp-context-*` services through `platform-libraries-bom`.

## Position in the hierarchy

```
cpp-platform-maven-parent-pom
└── cpp-platform-libraries  ← this project
    └── cpp-platform-maven-service-parent-pom
        └── [all cpp-context-* bounded-context services]
```

`cpp-platform-maven-service-parent-pom` inherits from this project and imports `platform-libraries-bom`, making all platform library versions available to every context service.

## Maven coordinates

| Property | Value |
|---|---|
| `groupId` | `uk.gov.moj.platform.libraries` |
| `artifactId` | `platform-libraries-parent-pom` |
| Parent | `uk.gov.moj.cpp.common:parent-pom` (cpp-platform-maven-parent-pom) |

## Modules

| Module | Artifact group | Description |
|---|---|---|
| `access-control-parent` | `access-control-*` | Drools-based access control evaluation. Loads `.drl` rule files from the classpath to enforce role-based access on incoming requests. Requires a `CJSCPPUID` header. |
| `audit-library-parent` | `audit-*` | Audit recording — writes an audit trail of commands and queries to the audit event store via JMS. Configurable action blacklist via `audit.blacklist` JNDI key. |
| `feature-control` | `feature-control-*` | Remote feature-flag control. Fetches flag values from a remote source and caches them locally. Master switch: `feature-control.enabled` JNDI key. |
| `metrics-micrometer` | `metrics-micrometer` | Micrometer metrics integration for context services — publishes to Azure Monitor. Configured via `micrometer.metrics.*` JNDI keys. |
| `healthchecks-parent` | `healthchecks-*` | MicroProfile health check base classes and WildFly probe registration. |
| `service-components` | `service-components` | Defines the `@ServiceComponent` values specific to the platform tier (beyond the core framework components). |
| `access-control-parent` | | |
| `id-mapper-client` | `id-mapper-client` | REST client for the ID mapper service — maps internal IDs to external references. |
| `system-documentgenerator-client` | `system-documentgenerator-client` | REST client for the document generator service. |
| `system-users-library` | `system-users-library` | Utilities for resolving user identity and roles from the `CJSCPPUID` session token. |
| `unifiedsearch-library-parent` | `unifiedsearch-*` | Client library for the unified search service (Elasticsearch-backed). |
| `activiti-parent` | `activiti-*` | Integration layer for Activiti BPMN workflow engine used in some bounded contexts. |
| `cpp-platform-library-utils` | `cpp-platform-library-utils` | Shared low-level utilities (string helpers, date converters, reflection utilities). |
| `platform-libraries-bom` | `platform-libraries-bom` | BOM that imports all platform library artifacts at a consistent version. |

## Build

```bash
mvn clean install -Denforcer.skip=true
```

The `enforce-moj-latest-interfaces` rule checks that MOJ interface JAR versions are current. During active development use `-Denforcer.skip=true`; this flag skips the standard `enforce-rules` execution but not rules with `<skip>false</skip>` hardcoded.

## JNDI configuration

See the `cp-microservice-framework` [jndi-configuration.md](../cp-microservice-framework/jndi-configuration.md) for `feature-control`, `audit.blacklist`, `micrometer.metrics.*`, and CORS keys — these originate in classes within `cp-microservice-framework` but apply to platform services using this library.
