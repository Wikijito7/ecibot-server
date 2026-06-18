---
name: monitoring-logging
description: "IMPORTANT: Load when adding monitoring, metrics, or logging. Covers Micrometer/Prometheus, SLF4J usage, CallLogging, and observability patterns."
---

## When to use me
- Adding custom metrics or monitoring
- Setting up logging in new classes
- Debugging production issues with logs
- Understanding the metrics endpoint

## Not intended for
- Error handling strategies → use `error-handling`
- Build/CI configuration → use `ci-cd-sonar`

---

## Logging

### SLF4J (Standard)

The project uses SLF4J with SLF4J Simple backend (configured in `build.gradle.kts`):
```kotlin
implementation(libs.slf4j.api)      // org.slf4j:slf4j-api
implementation(libs.slf4j.simple)    // org.slf4j:slf4j-simple
```

### Logger Declaration Pattern

```kotlin
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(MyClass::class.java)
```

Or inside a companion object (for services):
```kotlin
class EmailService(...) {
    companion object {
        private val logger = LoggerFactory.getLogger(EmailService::class.java)
    }
}
```

Ktor also provides `log` on `Application`:
```kotlin
import io.ktor.server.application.log

fun Application.someFunction() {
    log.info("Message")
    log.error("Error", exception)
}
```

### Log Levels

| Level | When to use |
|-------|-------------|
| `info` | Normal operations (task started, user registered) |
| `warn` | Unexpected but non-critical situations |
| `error` | Failures, exceptions, critical issues |
| `debug` | Development-only details (can be noisy) |

### NEVER use `println()`

All `println()` calls have been replaced with SLF4J. If you see one, replace it:
```kotlin
// BAD:
println(e.message)
println(e.stackTraceToString())

// GOOD:
logger.error("Failed to send email", e)
logger.error("Failed to save recover request", e)
```

### CallLogging (Request Logging)

Configured in `plugins/Monitoring.kt` to log every HTTP request at INFO level:
```kotlin
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
}
```

## Monitoring (Micrometer + Prometheus)

### Setup

```kotlin
val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

install(MicrometerMetrics) {
    registry = appMicrometerRegistry
    meterBinders = listOf(
        ClassLoaderMetrics(),
        JvmMemoryMetrics(),
        JvmGcMetrics(),
        ProcessorMetrics(),
        JvmThreadMetrics(),
        FileDescriptorMetrics(),
        UptimeMetrics()
    )
}
```

### Metrics Endpoint

Available at `GET /metrics` — returns Prometheus-formatted text:
```kotlin
routing {
    get("/metrics") {
        call.respondText {
            appMicrometerRegistry.scrape()
        }
    }
}
```

### Available Metrics (auto-collected)

| Metric | Source | Description |
|--------|--------|-------------|
| JVM memory | `JvmMemoryMetrics` | Heap, non-heap, buffers |
| GC | `JvmGcMetrics` | Garbage collection pauses |
| Threads | `JvmThreadMetrics` | Thread count, states |
| CPU | `ProcessorMetrics` | CPU usage |
| Classes | `ClassLoaderMetrics` | Loaded/unloaded classes |
| Uptime | `UptimeMetrics` | Application uptime |
| File descriptors | `FileDescriptorMetrics` | Open FD count |

### Adding Custom Metrics

```kotlin
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.micrometer.core.instrument.Counter

// Inject or access the registry:
val registry: PrometheusMeterRegistry = appMicrometerRegistry
val counter = Counter.builder("api.sounds.uploads")
    .description("Number of sound uploads")
    .register(registry)

// In your code:
counter.increment()
```

## Application Logging (`application.conf`)

Ktor application logging is configured via `application.conf`:

```hocon
ktor {
    // ...
}
```

SLF4J Simple logging can be configured via `src/main/resources/simplelogger.properties` or system properties:
```properties
org.slf4j.simpleLogger.defaultLogLevel=info
org.slf4j.simpleLogger.logFile=System.out
```

## Logging Best Practices

- Always include context in log messages: `logger.error("Failed to X for user {}", userId, e)`
- Never log sensitive data (passwords, tokens, secrets)
- Use parameterized logging: `logger.info("User {} logged in", username)` (not string concatenation)
- Log at the appropriate level: errors for failures, info for state changes, debug for details

## Blockers (MUST NOT)
- Using `println()` for logging — always use SLF4J
- Logging passwords, tokens, or personal data
- Catching exceptions without logging them (empty catch blocks)
- Using string concatenation in SLF4J — use `{}` placeholders
- Logging stack traces to stdout — use `logger.error("msg", exception)`

## References
- `plugins/Monitoring.kt` — Micrometer Prometheus + CallLogging setup
- `build.gradle.kts` — SLF4J dependencies
- `gradle/libs.versions.toml` — logback and SLF4J version references
- All data source files — real-world SLF4J usage examples
