---
name: api-routing
description: "IMPORTANT: Load when creating or modifying Ktor routes. Covers auth wrappers, JWT, TOTP, rate limiting, and response patterns."
---

## When to use me
- Creating or modifying a routing file in `es.wokis.routing`
- Adding auth guards (JWT, TOTP verification, email verification)
- Configuring rate limits
- Adding new API endpoints

## Not intended for
- Data layer changes → use `data-access`
- Koin wiring → use `data-access`

---

## Routing Structure

```
routing/
├── AuthRouting.kt
├── UserRouting.kt
├── RadioRouting.kt
├── SoundRouting.kt
├── StatsRouting.kt
└── utils/
    └── Wrappers.kt
```

All routes registered in `plugins/Routing.kt`:
```kotlin
fun Application.configureRouting() {
    routing {
        setUpAuthRouting()
        setUpUserRouting()
        setUpSoundRouting()
        setUpStatsRouting()
        setUpRadioRouting()
    }
}
```

## Auth Patterns (MUST)

### JWT Authentication (user must be logged in)
```kotlin
authenticate {
    get("/user") {
        val user = call.principal<UserBO>()
        // ...
    }
}
```

### TOTP (2FA) — requires TOTP code header
```kotlin
post("/2fa") {
    val user = call.principal<UserBO>()!!
    withAuthenticator(user) {
        // Requires TOTP code in request
    }
}
```
Use the `withAuthenticator` inline function from `TOTPService`.

### Email Verification Guard
```kotlin
post("/user/image") {
    val user = call.principal<UserBO>()!!
    verified(user) {
        // Only executes if user.emailVerified == true
    }
}
```
Use the `verified()` wrapper from `routing/utils/Wrappers.kt`.

## Rate Limiting
- **Default routes**: 100 requests per minute (configured in `plugins/RateLimit.kt`)
- **Auth routes** (`/login`, `/register`): Custom rate limit, 10 requests per minute
- Reference `RateLimitPluginConfiguration` in `plugins/RateLimit.kt`

## Response Pattern
```kotlin
call.respond(HttpStatusCode.OK, dtoObject)
call.respond(HttpStatusCode.Created, acknowledgeBO.toDTO())
call.respond(HttpStatusCode.ExpectationFailed, "User not verified")
```

## Route Setup Pattern
```kotlin
fun Route.setUpXxxRouting() {
    route("/api/path") {
        authenticate {
            get {
                // ...
            }
            post {
                // ...
            }
        }
    }
}
```

## Blockers (MUST NOT)
- Accessing repositories/datasources directly in routes — use repository interface
- Forgetting `authenticate {}` block on protected routes
- Missing TOTP check on 2FA-related endpoints
- Using raw DBO in route responses — always map to DTO

## References
- `src/main/kotlin/es/wokis/routing/` — existing routes
- `src/main/kotlin/es/wokis/plugins/Security.kt` — JWT setup
- `src/main/kotlin/es/wokis/services/TOTPService.kt` — TOTP logic
- `src/main/kotlin/es/wokis/routing/utils/Wrappers.kt` — `verified()` wrapper
