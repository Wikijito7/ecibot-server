---
name: testing-ktor
description: "CRITICAL: Load when writing tests. Project has ZERO test coverage — every new feature needs tests. Covers Ktor testApplication, MockK, kotlinx-coroutines-test, and JaCoCo."
---

## When to use me
- Writing tests for routes, repositories, or data sources
- Setting up test fixtures or mocks
- Configuring JaCoCo coverage
- Running tests locally or in CI

## Not intended for
- Build/CI configuration → use `ci-cd-sonar`
- Production code patterns → use `data-access` or `api-routing`

---

## Testing Framework

This project uses:
- **Kotlin Test** (JUnit 5 platform)
- **MockK** for mocking (`io.mockk:mockk:1.14.2`)
- **Kotlinx Coroutines Test** for coroutine testing (`kotlinx-coroutines-test:1.10.1`)

Mirrors what ECIBotKt uses — consistent across the ecosystem.

## Test Dependencies

### `gradle/libs.versions.toml`
```toml
[versions]
mockk = "1.14.2"
kotlin-coroutines-test = "1.10.1"
junit-parametized = "5.13.0"

[libraries]
kotlin-test = { module = "org.jetbrains.kotlin:kotlin-test", version.ref = "kotlin" }
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
kotlin-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlin-coroutines-test" }
junit-parametrized = { module = "org.junit.jupiter:junit-jupiter-params", version.ref = "junit-parametized" }
```

### `build.gradle.kts`
```kotlin
testImplementation(libs.kotlin.test)
testImplementation(libs.mockk)
testImplementation(libs.kotlin.coroutines.test)
testImplementation(libs.junit.parametrized)
```

## Test Structure

```
src/test/kotlin/es/wokis/
├── routing/        # Route tests (Ktor testApplication)
├── repository/     # Repository unit tests (MockK)
├── datasource/     # Data source tests (fakes or in-memory)
├── service/        # Service unit tests
└── utils/          # Test helpers and fixtures
```

## Writing Tests

### Basic Test Structure
```kotlin
package es.wokis.routing

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SoundRoutingTest {

    @Test
    fun `Given X When Y Then Z`() = runTest {
        // Given - Setup mocks and test data

        // When - Execute the action

        // Then - Verify results
    }
}
```

### Mocking Services/Repositories
```kotlin
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

private val soundRepository: SoundRepository = mockk()

@BeforeEach
fun setup() {
    coEvery { soundRepository.getAllSounds() } returns listOf(soundBO)
    coEvery { soundRepository.getSoundById(any()) } returns soundBO
}
```

### Mockk Functions

| Function | Use For |
|----------|---------|
| `every { }` | Regular (non-suspend) functions |
| `coEvery { }` | Suspend functions |
| `verify { }` | Verify regular calls |
| `coVerify { }` | Verify suspend calls |
| `mockk()` | Create mock objects |
| `mockk<T>(relaxed = true)` | Create mock with default returns |

### Testing Coroutines
All async tests must use `runTest`:
```kotlin
@Test
fun `Given valid input When execute Then success`() = runTest {
    // Test code here
}
```

### Route Testing with Ktor `testApplication`
```kotlin
@Test
fun `Given authenticated request When GET sounds Then return list`() = testApplication {
    environment {
        config = MapApplicationConfig(
            "jwt.realm" to "test",
            "jwt.domain" to "test",
            "jwt.audience" to "test"
        )
    }

    application {
        configureKoin()
        // override modules with test doubles
    }

    val response = client.get("/sounds") {
        bearerAuth("test-token")
    }

    assertEquals(HttpStatusCode.OK, response.status)
}
```

### Repository Testing (MockK)
```kotlin
class SoundRepositoryTest {
    private val mockDataSource = mockk<SoundLocalDataSource>()
    private val repository = SoundRepositoryImpl(mockDataSource)

    @Test
    fun `Given valid sound When addSound Then return acknowledge`() = runTest {
        // Given
        coEvery { mockDataSource.insertSound(any()) } returns true

        // When
        val result = repository.addSound(sound, user)

        // Then
        assertTrue(result.acknowledge)
        coVerify { mockDataSource.insertSound(any()) }
    }
}
```

### Data Source Testing (Fakes)
```kotlin
class FakeSoundDataSource : SoundLocalDataSource {
    val sounds = mutableListOf<SoundDBO>()

    override suspend fun getAllSounds(): List<SoundDBO> = sounds
    override suspend fun insertSound(sound: SoundDBO): Boolean = sounds.add(sound)
}
```

### Testing Error Handling
```kotlin
@Test
fun `Given error When getSounds Then return empty list`() = runTest {
    // Given
    coEvery { soundRepository.getAllSounds() } throws RuntimeException()

    // When
    val result = service.getSounds()

    // Then
    assertTrue(result.isEmpty())
}
```

## Test Naming Conventions

Use descriptive names following the pattern:
```
Given [context] When [action] Then [expected result]
```

Examples:
- `Given unauthenticated request When GET /sounds Then return 401`
- `Given valid multipart When POST /sound Then return 201`
- `Given non-existent id When GET /sound/{id} Then return 404`

## Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "SoundRoutingTest"

# Run with pattern
./gradlew test --tests "*Sound*"

# Generate coverage report
./gradlew jacocoTestReport

# View coverage
open build/reports/jacoco/test/html/index.html
```

## Coverage Requirements
- **Minimum**: 80% line coverage for new code
- **Target**: 100% coverage for critical paths
- **Exceptions**: BO, DTO, Exception, Application.kt, DI modules are excluded from SonarCloud

## Test Categories

### Unit Tests
- Test individual classes in isolation
- Mock all dependencies
- Fast execution (< 100ms each)

### Integration Tests
- Test service interactions
- Mock external APIs only (DB, email, HTTP)
- Use `testApplication {}` for route-level testing

## Blockers (MUST NOT)
- Writing tests that call real external services (DB, email, HTTP) — always mock
- Using `println()` in tests — use test assertions
- Forgetting `runTest {}` for suspend functions
- Testing implementation details (private methods)
- Relying on test execution order
- Skipping JaCoCo exclusions (BO/DTO/Exception files are intentionally excluded)

## References
- `build.gradle.kts` — existing test dependencies and JaCoCo config
- `gradle/libs.versions.toml` — version catalog
- `ECIBotKt/.github/instructions/testing.instructions.md` — sibling project testing conventions
- Mockk docs: https://mockk.io/
- Kotlin coroutines testing: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
