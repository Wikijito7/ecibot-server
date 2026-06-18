---
name: config-management
description: "Load when adding new configuration properties, modifying HOCON files, or understanding how config is loaded. Covers app.conf, application.conf, and the loading pipeline."
---

## When to use me
- Adding new configuration properties (API keys, file paths, timeouts)
- Understanding how config files are loaded at startup
- Modifying `app.conf` or `application.conf`
- Setting up local development configuration

## Not intended for
- CI/CD configuration → use `ci-cd-sonar`
- Environment variables or secrets management for production

---

## Config Architecture

Two config files serve different purposes:

### 1. `application.conf` (HOCON — Ktor config)
Location: `src/main/resources/application.conf`

Used for Ktor server settings:
```
ktor {
    deployment {
        host = "0.0.0.0"
        port = 8080
    }
    security {
        jwt {
            realm = "..."       // accessed via environment.config.property("jwt.realm")
            domain = "..."      // accessed via environment.config.property("jwt.domain")
            audience = "..."    // accessed via environment.config.property("jwt.audience")
        }
    }
}
```

Loaded via `HoconApplicationConfig(ConfigFactory.load("application.conf"))` in `Application.kt`.

### 2. `app.conf` (HOCON — application config)
Location: `src/main/resources/app.conf` → copied to `config/app.conf` on first run

Contains domain-specific settings:
```hocon
secretKey = "pestillo"
db {
    ip = "localhost"
    port = "27017"
    user = "user"
    password = "pass"
    databaseName = "ecibot"
}
baseUri = "https://api.wokis.es/ecibot"
imageFolder = "images/"
mail { user = "test@test.es", pass = "abc123." }
firebaseSdkDir = "config/firebaseSdk.json"
google { clientId = "...", clientSecret = "..." }
issuer = "ECIBot"
```

## Config Loading Pipeline

```
Application startup (Application.kt)
  ↓
initConfig() (plugins/Config.kt)
  ├─ Copies app.conf from resources to config/app.conf (if not exists)
  ├─ Copies words.txt from resources to config/words.txt (if not exists)
  └─ Parses config/app.conf via ConfigFactory.parseFile()
       ↓
Config available globally via: es.wokis.plugins.config
```

## Accessing Config Values

```kotlin
import es.wokis.plugins.config

val simpleValue = config.getString("key")
val nestedValue = config.getString("db.databaseName")
val baseUri = config.getString("baseUri")
val issuer = config.issuer  // Uses extension property from Config.kt
```

## Adding a New Config Property

1. Add to `src/main/resources/app.conf`:
```hocon
myFeature {
    folder = "my-feature-files/"
    enabled = true
}
```

2. Access in code:
```kotlin
val folder = config.getString("myFeature.folder")
val enabled = config.getBoolean("myFeature.enabled")
```

## Local Development Setup

Config files are auto-copied from resources on first run:
```
project-root/
├── config/
│   ├── app.conf          # Local override (gitignored)
│   └── words.txt         # TOTP recovery words
└── src/main/resources/
    ├── application.conf  # Ktor config
    ├── app.conf          # Default app config (template)
    └── words.txt         # Default words
```

After first run, edit `config/app.conf` for local settings (MongoDB credentials, etc.).

## Blockers (MUST NOT)
- Hardcoding environment-specific secrets in source files — use local `config/app.conf`
- Accessing `config` before `initConfig()` is called (it's a `lateinit var`)
- Using `application.conf` for domain-specific settings — use `app.conf` instead
- Storing secrets in the default `app.conf` that gets committed — use environment-specific overrides
- Forgetting to copy new properties to the default `app.conf` in resources

## References
- `plugins/Config.kt` — config loading implementation
- `Application.kt` — module initialization order
- `src/main/resources/app.conf` — default application config
- `src/main/resources/application.conf` — Ktor config
- Every file that uses `config.getString(...)` for real-world examples
