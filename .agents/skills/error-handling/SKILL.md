---
name: error-handling
description: "IMPORTANT: Load when adding error handling in routes, services, or repositories. Covers AcknowledgeBO vs exceptions, standardized responses, and proper logging."
---

## When to use me
- Handling errors in routes (try-catch, response codes)
- Deciding between throwing exceptions vs returning AcknowledgeBO
- Adding new custom exceptions
- Standardizing error responses

## Not intended for
- General route setup → use `api-routing`
- Data layer operations → use `data-access`

---

## Two Error Handling Strategies

This project uses a mix of two strategies — use the right one for the right scenario:

### 1. Exceptions (for unexpected/invariant violations)

Thrown from repositories or services, caught in routes:

```kotlin
// data/exception/CustomExceptions.kt
object UserNotFoundException : IllegalStateException()
object PasswordConflictException : IllegalStateException()
object UsernameAlreadyExistsException : IllegalStateException()
object EmailAlreadyExistsException : IllegalStateException()
object VerificationNotFoundException : IllegalStateException()
object TotpNotFoundException : IllegalStateException()
object RecoverCodeNotFoundException : IllegalStateException()
```

All exceptions are:
- **`object` singletons** extending `IllegalStateException`
- **Named descriptively**: `WhatHappenedException`
- **Thrown in repositories**, caught in routes

**When to use exceptions**: For truly exceptional cases that shouldn't happen under normal flow (e.g., user not found during login, duplicate username on registration).

### 2. AcknowledgeBO (for expected failures)

For operations where failure is a valid outcome (e.g., update returning false):

```kotlin
data class AcknowledgeBO(val acknowledge: Boolean)

// Mapped to response:
fun AcknowledgeBO.toDTO() = AcknowledgeDTO(acknowledge)
```

**When to use AcknowledgeBO**: For operations where "did it work?" is a valid question (e.g., save, delete, update operations that might fail for expected reasons).

## Route Error Handling Patterns

### Catching specific exceptions
```kotlin
try {
    repository.someOperation(user)
    call.respond(HttpStatusCode.OK)
} catch (exc: UserNotFoundException) {
    call.respond(HttpStatusCode.NotFound, "User not found")
}
```

### Catching generic exceptions (fallback)
```kotlin
try {
    val result = repository.updateUser(callUser, updatedUser.toBO())
    call.respond(HttpStatusCode.OK, result.toDTO())
} catch (exc: Exception) {
    call.respond(HttpStatusCode.Conflict, "Username already exists")
}
```

### No catch (let Ktor handle it)
For truly unexpected errors, don't catch — Ktor returns 500 Internal Server Error automatically.

### AcknowledgeBO in routes
```kotlin
val acknowledged = repository.someOperation(user)
call.respond(HttpStatusCode.OK, acknowledged.toDTO())
```

## Response Status Codes

| Code | When to use |
|------|-------------|
| `200 OK` | Success with body |
| `201 Created` | Resource created successfully |
| `400 Bad Request` | Missing/invalid parameters |
| `401 Unauthorized` | Not authenticated |
| `403 Forbidden` | Not authorized (TOTP/email not verified) |
| `404 Not Found` | Resource not found |
| `409 Conflict` | Duplicate resource (username/email taken) |
| `415 Unsupported Media Type` | Wrong content type in upload |
| `422 Unprocessable Entity` | Valid request but can't process |
| `500 Internal Server Error` | Unexpected error (don't send details) |

## Error Response Body Conventions

- **Simple string**: `call.respond(HttpStatusCode.NotFound, "User not found")`
- **DTO**: `call.respond(HttpStatusCode.OK, dtoObject)`
- **Acknowledge**: `call.respond(HttpStatusCode.OK, acknowledged.toDTO())`

## Logging Errors

Use SLF4J (never `println()`):
```kotlin
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(MyClass::class.java)

// In catch blocks:
catch (e: SomeException) {
    logger.error("Meaningful message about what failed", e)
}
```

## Adding a New Custom Exception

```kotlin
// In data/exception/CustomExceptions.kt:
object SoundNotFoundException : IllegalStateException()
```

Keep them as `object` singletons. Only create parameterized exceptions if you need dynamic error messages.

## Blockers (MUST NOT)
- Using `println()` for error logging — always use SLF4J
- Throwing exceptions for control flow — use `AcknowledgeBO` instead
- Sending internal error details (stack traces) to clients
- Catching exceptions and doing nothing with them (empty catch blocks)
- Mixing exception types inconsistently in the same route

## References
- `data/exception/CustomExceptions.kt` — all custom exceptions
- `data/bo/response/AcknowledgeBO.kt` — acknowledge model
- `data/dto/response/AcknowledgeDTO.kt` — acknowledge API contract
- `data/mapper/acknowledge/AcknowledgeMapper.kt` — acknowledge mapper
- `routing/UserRouting.kt` — real-world try-catch examples
- `routing/AuthRouting.kt` — exception handling in auth flow
