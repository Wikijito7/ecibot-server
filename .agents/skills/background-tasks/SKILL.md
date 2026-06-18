---
name: background-tasks
description: "Load when adding scheduled tasks, periodic jobs, or background coroutines. Covers the coroutine-based task pattern, error handling, and lifecycle management."
---

## When to use me
- Adding periodic/background data fetching (like radio stations)
- Scheduling recurring maintenance tasks
- Running async operations at application startup

## Not intended for
- Request-scoped async operations → use standard coroutines in routes
- One-off background operations → use `launch` in route handlers

---

## Current Pattern

Tasks are launched at application startup in `plugins/Tasks.kt`:

```kotlin
fun Application.configureTasks() {
    launch(Dispatchers.IO) {
        registerPeriodicStationRequest()
    }
}
```

Task functions are defined in `tasks/` package:

```kotlin
// tasks/StationsTasks.kt
suspend fun Application.registerPeriodicStationRequest() {
    val radioRepository by inject<RadioRepository>()

    while (true) {
        log.info("Auto fetching remote radios")
        radioRepository.fetchAndSaveRemoteRadios()
        delay(RADIO_AUTO_FETCH_TIMEOUT) // 24h + 10s
    }
}
```

## Pattern Anatomy

```
Application startup
  ↓
configureTasks() → launch(Dispatchers.IO) { taskFunction() }
  ↓
taskFunction()
  ├── inject dependencies via `by inject<>()`
  └── infinite loop with delay:
       ├── do work
       ├── handle errors (catch + log)
       └── delay(interval)
```

## Error Handling

The current implementation does NOT catch errors inside the loop — a single exception crashes the whole task. Always wrap the loop body:

```kotlin
suspend fun Application.registerPeriodicStationRequest() {
    val radioRepository by inject<RadioRepository>()

    while (true) {
        try {
            log.info("Auto fetching remote radios")
            radioRepository.fetchAndSaveRemoteRadios()
        } catch (e: Exception) {
            log.error("Background task failed, will retry", e)
        }
        delay(RADIO_AUTO_FETCH_TIMEOUT)
    }
}
```

## Adding a New Background Task

1. Create task function in `tasks/YourTask.kt`:
```kotlin
package es.wokis.tasks

import es.wokis.data.repository.sound.SoundRepository
import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.delay
import org.koin.ktor.ext.inject

private const val MY_TASK_INTERVAL = 60 * 60 * 1000L  // 1 hour

suspend fun Application.registerPeriodicTask() {
    val repository by inject<SoundRepository>()

    while (true) {
        try {
            log.info("Running periodic task")
            repository.someOperation()
        } catch (e: Exception) {
            log.error("Periodic task failed", e)
        }
        delay(MY_TASK_INTERVAL)
    }
}
```

2. Wire it in `plugins/Tasks.kt`:
```kotlin
fun Application.configureTasks() {
    launch(Dispatchers.IO) {
        registerPeriodicStationRequest()
        registerPeriodicTask()
    }
}
```

## Known Issues / Improvements Needed

- **No structured concurrency**: Coroutines are launched without a parent Job or scope
- **No graceful shutdown**: Tasks don't handle cancellation — use `isActive` check:
  ```kotlin
  while (isActive) { ... }
  ```
- **No configurable intervals**: Timeout is hardcoded — should come from `app.conf`
- **No lifecycle hooks**: Tasks run forever until the JVM is killed
- **Duplicate constants**: `24 * 60 * 60 * 1000L + 10000` appears in both `tasks/` and data sources

## Blockers (MUST NOT)
- Using `Thread.sleep()` in tasks — always use `kotlinx.coroutines.delay`
- Launching tasks without error handling — one exception kills the entire loop
- Using `runBlocking` in tasks — use `launch(Dispatchers.IO)` instead
- Hardcoding long intervals in multiple places — use a shared constant or config

## References
- `plugins/Tasks.kt` — task registration
- `tasks/StationsTasks.kt` — actual task implementation
- `data/repository/radio/RadioRepository.kt` — task's target operation
