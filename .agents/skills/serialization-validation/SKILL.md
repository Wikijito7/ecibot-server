---
name: serialization-validation
description: "Load when creating DTOs, configuring JSON serialization, or adding input validation. Covers @SerialName conventions, JSON config, and validation patterns."
---

## When to use me
- Creating or modifying DTO classes with kotlinx.serialization
- Configuring JSON content negotiation
- Adding input validation for API requests
- Mapping JSON field names

## Not intended for
- Database models (DBO) → use `data-access`
- General data layer → use `data-access`

---

## Kotlinx Serialization Conventions

### DTO Naming
- All DTOs use `@SerialName` on **every field** (mandatory):
```kotlin
@Serializable
data class UserDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("image") val image: String,
    @SerialName("lang") val lang: String = "en",
)
```

This ensures consistent JSON field naming regardless of Kotlin property names.

### @Serializable
All DTOs must be annotated with `@Serializable`:
```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class AcknowledgeDTO(
    @SerialName("acknowledge") val acknowledge: Boolean
)
```

### JSON Config
Currently configured with defaults in `plugins/Serialization.kt`:
```kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
```

For custom JSON behavior (e.g., pretty print, ignore unknown keys):
```kotlin
json(Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
})
```

## Request Body Deserialization

Ktor automatically deserializes request bodies to DTOs:
```kotlin
post {
    val request = call.receive<SoundRequestDTO>()
    // request.title, request.sound
}
```

## Validation Patterns

### Manual validation with extension functions
```kotlin
// utils/PatternUtils.kt
fun String.isEmail(): Boolean =
    Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matcher(this).matches()

// Usage in routes:
if (!email.isEmail()) {
    call.respond(HttpStatusCode.BadRequest, "Invalid email")
    return@post
}
```

### Guard clause pattern in routes
```kotlin
post("/register") {
    val registerDTO = call.receive<RegisterDTO>()
    
    if (registerDTO.username.isBlank()) {
        return@post call.respond(HttpStatusCode.BadRequest, "Username is required")
    }
    if (!registerDTO.email.isEmail()) {
        return@post call.respond(HttpStatusCode.BadRequest, "Invalid email")
    }
    
    // Proceed with valid data...
}
```

### Required parameter validation
```kotlin
// utils/RouteExtensions.kt
suspend fun RoutingContext.requireString(fieldName: String, block: suspend RoutingContext.(String) -> Unit) {
    call.parameters[fieldName]?.takeIf { it.isNotEmpty() }?.let {
        block(it)
    } ?: call.respond(HttpStatusCode.BadRequest, "$fieldName is required")
}

// Usage:
get {
    requireString("id") { id ->
        // id is guaranteed non-null and non-empty
    }
}
```

### Required integer parameter validation
```kotlin
suspend fun RoutingContext.requireInt(fieldName: String, block: suspend RoutingContext.(Int) -> Unit) {
    call.parameters[fieldName]?.takeIf { it.isNotEmpty() }?.toIntOrNull()?.let {
        block(it)
    } ?: call.respond(HttpStatusCode.BadRequest, "$fieldName is required")
}
```

## Validation Checklist for New Endpoints

- [ ] DTO fields use `@SerialName` for JSON mapping
- [ ] DTO class is annotated with `@Serializable`
- [ ] Request body validated before use (not empty, correct format)
- [ ] Email validated with `String.isEmail()` if present
- [ ] URL parameters extracted with `requireString`/`requireInt`
- [ ] File uploads validated by content type starts with `"audio/"` or `"image/"`
- [ ] Response DTOs exclude internal fields (passwords, secrets)
- [ ] Default values used for optional fields where appropriate

## Blockers (MUST NOT)
- Missing `@Serializable` on DTOs (will cause runtime serialization errors)
- Using `@SerialName` inconsistently (some fields with, some without)
- Returning raw DBO/BO objects from routes — always map to DTO
- Trusting user input without validation — validate before processing
- Using field names as JSON keys without explicit `@SerialName`

## References
- `plugins/Serialization.kt` — JSON content negotiation setup
- `data/dto/` — all existing DTOs with `@SerialName` patterns
- `utils/PatternUtils.kt` — email validation
- `utils/RouteExtensions.kt` — parameter validation utilities
- `routing/AuthRouting.kt` — real-world validation examples
