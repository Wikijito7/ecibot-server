---
name: project-architecture
description: "IMPORTANT: Load when onboarding, planning a new feature, or understanding how the project is wired end-to-end. Covers the full stack: startup → DI → data → routing → config."
---

## When to use me
- Starting work on the project for the first time
- Planning a new domain or feature (e.g., sounds, playlists, moderation)
- Debugging wiring issues (DI, config, module loading)
- Understanding how data flows from HTTP → DB → response

## Not intended for
- Implementing specific routes → use `api-routing`
- Data layer changes → use `data-access`
- File uploads → use `file-upload`
- Social features → use `social-reaction`

---

## Big Picture

```
┌─────────────────────────────────────────────────┐
│  Ktor (Netty) Embedded Server                   │
│  Application.kt → Application.module()          │
│                                                   │
│  Module loading order (CRITICAL):                 │
│  1. initConfig()        — HOCON config           │
│  2. configureKoin()     — DI setup              │
│  3. configureSerialization() — JSON             │
│  4. configureMonitoring()  — Metrics + logs     │
│  5. configureHTTP()        — CORS               │
│  6. configureSecurity()   — JWT auth            │
│  7. configureRateLimit()  — Rate limiting       │
│  8. configureRouting()    — Route aggregation   │
│  9. configureTasks()      — Background jobs     │
└─────────────────────────────────────────────────┘
```

## Project Layout

```
src/main/kotlin/es/wokis/
├── Application.kt              # Entry point + module()
├── plugins/                    # Ktor plugins (config, DI, security, routing...)
│   ├── Config.kt               # HOCON config loader (app.conf)
│   ├── Koin.kt                 # Koin DI wiring
│   ├── Security.kt             # JWT auth + token generation
│   ├── RateLimit.kt            # Global + auth rate limits
│   ├── Routing.kt              # Route aggregator
│   ├── HTTP.kt                 # CORS
│   ├── Serialization.kt        # JSON content negotiation
│   ├── Monitoring.kt           # Prometheus + call logging
│   └── Tasks.kt                # Background coroutine jobs
├── routing/                    # Route handlers (one file per domain)
│   ├── AuthRouting.kt          # /login, /register, /verify, /recover
│   ├── UserRouting.kt          # /user, /users, avatar, 2FA
│   ├── SoundRouting.kt         # /sounds, /sound
│   ├── RadioRouting.kt         # /radio (paginated, search, country codes)
│   ├── StatsRouting.kt         # /stats
│   └── utils/Wrappers.kt       # verified() guard
├── di/                         # Koin modules
│   ├── DataSourceModule.kt     # DB collections + HTTP client + data sources
│   ├── RepositoryModule.kt     # Business logic repositories
│   └── ServiceModule.kt        # Services (email, TOTP, file storage)
├── data/
│   ├── dto/{domain}/           # API contracts (@Serializable)
│   ├── bo/{domain}/            # Business objects (domain models)
│   ├── dbo/                    # MongoDB documents (@BsonId, @SerialName)
│   ├── mapper/{domain}/        # Extension-function mappers: DTO↔BO↔DBO
│   ├── datasource/
│   │   ├── local/{domain}/     # MongoDB CRUD
│   │   └── remote/{domain}/    # Ktor HTTP client for external APIs
│   ├── repository/             # Interface + Impl, orchestrates datasources
│   ├── constants/              # ServerConstants (EMPTY_TEXT, etc.)
│   └── exception/              # Singleton exceptions
├── services/                   # Business services
│   ├── EmailService.kt         # Jakarta Mail
│   ├── ImageService.kt         # Avatar file storage
│   └── TOTPService.kt          # 2FA setup/remove
└── utils/                      # Extensions & helpers
    ├── RouteExtensions.kt      # requireString(), requireInt()
    ├── PaginationExtensions.kt # paginated { page -> }
    ├── CredentialsUtils.kt     # ApplicationCall.user extension
    ├── MultipartExtensions.kt  # getAllParts()
    ├── HashGenerator.kt        # generateHash(), generateHashWithSeed()
    ├── StringUtils.kt          # normalizeUrl(), getRandomWords()
    ├── DateUtils.kt            # toDate(), toStringFormatted()
    ├── BooleanUtils.kt         # Boolean?.isTrue() / isFalse()
    ├── ListUtils.kt            # takeAtMost()
    ├── PatternUtils.kt         # String.isEmail()
    └── FileUtils.kt            # Other helpers
```

## Config (`app.conf`)

Loaded from `config/app.conf` (auto-copied from resources on first run):
```hocon
secretKey = "pestillo"
db { ip, port, user, password, databaseName }
baseUri = "https://api.wokis.es/ecibot"
imageFolder = "images/"
mail { user, pass }
firebaseSdkDir = "config/firebaseSdk.json"
google { clientId, clientSecret }
issuer = "ECIBot"
```

Access via:
```kotlin
import es.wokis.plugins.config
val value = config.getString("path.to.key")
```

## Data Layer Flow

```
HTTP Request  ←→  DTO (@Serializable, @SerialName)
                      ↕ Mapper (.toBO() / .toDTO())
                   BO (domain model, implements Principal for auth)
                      ↕ Mapper (.toDBO() / .toBO())
                   DBO (MongoDB document, @BsonId)
                      ↓
                DataSource (MongoCollection CRUD)
                      ↓
                Repository (orchestrates datasources + business logic)
```

## Auth Flow

```
1. User logs in → JWT token generated with claims: { id, session, timestamp }
2. Token sent in `Authorization: Bearer <token>` header
3. configureSecurity() validates JWT → extracts user from DB via UserRepository
4. Returns UserBO as Principal (available via call.principal<UserBO>() or call.user)
5. Routes protected by `authenticate { }` block
6. Email verification: `verified(user) { }` wrapper checks user.emailVerified
7. 2FA/TOTP: `withAuthenticator(user) { }` wrapper requires TOTP code header
```

## Adding a New Feature (Checklist)

When creating a new domain (e.g., "sounds", "playlists"):

1. **Config**: Add storage paths to `app.conf` if needed
2. **DB**: Add Mongo collection in `AppDataBase.kt` + `DataSourceModule.kt`
3. **Models**: Create DTO, BO, DBO in `data/dto/*`, `data/bo/*`, `data/dbo/`
4. **Mapper**: Create extension-function mappers in `data/mapper/*/Mapper.kt`
5. **DataSource**: Create `*LocalDataSource` (interface + impl) in `data/datasource/local/*/`
6. **Repository**: Create `*Repository` (interface + impl) in `data/repository/*/`
7. **Service**: If file storage needed, create service in `services/`
8. **DI**: Wire everything in `DataSourceModule.kt`, `RepositoryModule.kt`, `ServiceModule.kt`
9. **Route**: Create `*Routing.kt` in `routing/`, register in `plugins/Routing.kt`
10. **Rate limit**: Add custom rate limit names in `plugins/RateLimit.kt` if needed

## Utilities Reference

| Utility | Location | Purpose |
|---------|----------|---------|
| `call.user` | `utils/CredentialsUtils.kt` | Get authenticated UserBO |
| `paginated { page -> }` | `utils/PaginationExtensions.kt` | Add `/page/{page}` to routes |
| `requireString("name")` | `utils/RouteExtensions.kt` | Safe URL param extraction |
| `requireInt("id")` | `utils/RouteExtensions.kt` | Safe int param extraction |
| `getAllParts()` | `utils/MultipartExtensions.kt` | Collect multipart parts |
| `generateHashWithSeed()` | `utils/HashGenerator.kt` | Generate unique display IDs |
| `normalizeUrl()` | `utils/StringUtils.kt` | Create URL-safe file paths |
| `String.isEmail()` | `utils/PatternUtils.kt` | Email validation |
| `.toDTO()` / `.toBO()` / `.toDBO()` | `data/mapper/*/` | Layer conversion |

## Monitoring & Metrics
- Micrometer Prometheus registry at `/metrics`
- Call logging with Ktor's CallLogging plugin
- Background tasks configured in `plugins/Tasks.kt`

## References
- `Application.kt` — module loading order
- `plugins/` — all Ktor plugin configurations
- `di/` — Koin module definitions
- `routing/` — existing route implementations
- `data/` — existing domain implementations
- `utils/` — reusable extensions and helpers
