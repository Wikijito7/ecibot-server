---
name: data-access
description: "CRITICAL: Load when touching *BO.kt, *DBO.kt, *DTO.kt, *Mapper.kt, *Repository.kt, or *DataSource.kt. Wrong layer boundaries = immediate PR rejection."
---

## When to use me
- Creating or modifying a **DTO**, **BO**, **DBO**, **Mapper**, **Repository**, or **DataSource**
- Wiring data layer in a **Koin module**
- Adding MongoDB collections or queries

## How to detect context
- Files in `data/dto/`, `data/bo/`, `data/dbo/`, `data/mapper/`, `data/repository/`, `data/datasource/`
- Classes named `*DTO`, `*BO`, `*DBO`, `*Mapper`, `*Repository`, `*DataSource`
- Koin modules in `di/DataSourceModule.kt`, `di/RepositoryModule.kt`

## Architecture Rules (MUST)

### Layer Structure
```
DTO (API contract, kotlinx.serialization @Serializable)
  ↕ Mapper (extension functions)
BO (business object, domain model)
  ↕ Mapper (extension functions)
DBO (MongoDB document, org.bson.types.ObjectId)
  ↓
DataSource (local= MongoDB / remote= Ktor HttpClient)
  ↓
Repository (interface + impl, orchestrates datasources)
```

### Mapper Conventions
- **Extension functions only**, never class-based mappers
- File: `data/mapper/{entity}/Mapper.kt`
- Named by direction: `DTO.toBO()`, `BO.toDBO()`, `DBO.toBO()`, `BO.toDTO()`
- List overloads: `@JvmName("${type}To${Type}") fun List<A>.toB() = this.map { it.toB() }`

### DBO Conventions
- Located in `data/dbo/{entity}/`
- Uses `org.bson.types.ObjectId` for `_id`
- Matches MongoDB document shape exactly
- `@Serializable` with kotlinx.serialization

### BO Conventions
- Located in `data/bo/{entity}/`
- Domain model, extends `Principal` when used as auth principal
- Custom `equals`/`hashCode` for `ByteArray` fields (e.g., `totpEncodedSecret`)

### DTO Conventions
- Located in `data/dto/{entity}/`
- `@Serializable` with kotlinx.serialization
- API request/response contracts
- Use `@SerialName` for JSON field mapping

### DataSource Conventions
- Interface + Impl in same file
- **Local**: `data/datasource/local/{entity}/*LocalDataSource*` (MongoDB)
- **Remote**: `data/datasource/remote/{entity}/*RemoteDataSource*` (Ktor HTTP Client)
- Never name as `*ApiDataSource` — use `*RemoteDataSource`

### Repository Conventions
- Interface + Impl in same file at `data/repository/{entity}/*Repository*`
- Return `AcknowledgeBO` for write operations
- Throw domain exceptions from `data/exception/CustomExceptions.kt`
- Accept `UserBO` for auth context

### DI Wiring (Koin)
```kotlin
// DataSourceModule.kt
single<XLocalDataSource> { XLocalDataSourceImpl(get(named("collectionName"))) }

// RepositoryModule.kt
single<XRepository> { XRepositoryImpl(get()) }
```

## Blockers (MUST NOT)
- Creating class-based mappers → use extension functions
- Throwing exceptions for control flow → use `AcknowledgeBO`
- Naming `*ApiDataSource` → use `*RemoteDataSource`
- Bypassing repository layer from routing
- Mixing DTO/DBO responsibilities

## References
- `.github/instructions/` — no instructions file exists for this project
- See existing mappers in `src/main/kotlin/es/wokis/data/mapper/`
