---
name: ci-cd-sonar
description: "Load when configuring build pipeline, CI workflows, SonarCloud, JaCoCo, or the Gradle version catalog. Covers fatJar, ktlint, and dependency management."
---

## When to use me
- Setting up or modifying CI/CD workflows in `.github/workflows/`
- Adding/updating dependencies in `gradle/libs.versions.toml`
- Configuring SonarCloud or JaCoCo
- Building the fat JAR
- Debugging Gradle build issues

## Not intended for
- Writing tests → use `testing-ktor`
- Day-to-day coding → use `data-access` or `api-routing`

---

## CI/CD Workflows

### ktlint (code style)

File: `.github/workflows/ktlint.yaml`
- Runs on every pull request
- Uses `ScaCap/action-ktlint@master`
- Fails on error (`fail_on_error: true`)
- Reports via GitHub PR review comments
- Enforces `intellij_idea` code style (configured in `.editorconfig`)

### SonarCloud (build + analysis)

File: `.github/workflows/sonarcloud-analysis.yaml`
- Runs on push to `master` and on every PR
- Java 17 (Zulu JDK)
- Caches SonarCloud and Gradle packages
- Runs: `./gradlew build sonar --info`
- Requires `SONAR_TOKEN`, `SONAR_PROJECT_KEY`, `SONAR_ORGANIZATION` secrets/vars
- Uses `fetch-depth: 0` for full git history (required for Sonar analysis)

## Gradle Version Catalog (`gradle/libs.versions.toml`)

All dependencies are managed via version catalog. Structure:

```toml
[versions]
ktor = "3.1.3"
kotlin = "2.1.20"
# ...

[libraries]
ktor-server-core = { module = "io.ktor:ktor-server-core-jvm", version.ref = "ktor" }
# ...

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
# ...
```

### Conventions
- Library aliases use kebab-case matching Gradle accessor convention (`ktor-server-core` → `libs.ktor.server.core`)
- Ktor modules use `-jvm` classifier (except client modules and rate-limit)
- Test dependency `ktor-server-tests` uses pinned version `2.3.13` (different from main Ktor version)
- All versions in `[versions]` block, referenced by `version.ref`

## SonarCloud Exclusions

```kotlin
sonar {
    properties {
        property("sonar.coverage.exclusions", listOf(
            "**/*BO.kt",
            "**/*DTO.kt",
            "**/*Exception.kt",
            "src/main/kotlin/es/wokis/Application.kt",
            "*.kts",
            "**/di/*.kt",
        ))
    }
}
```
BO, DTO, Exception, Application.kt, build scripts, and DI modules are excluded from coverage analysis.

## JaCoCo Coverage

Configured in `build.gradle.kts`:
```kotlin
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        xml.outputLocation.set(file("build/reports/jacoco/test-results/jacocoTestReport.xml"))
    }
}
```

## Fat JAR Build

Uses Ktor Gradle plugin for fat JAR:
```kotlin
ktor {
    fatJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }
}
```

Run with:
```bash
./gradlew fatJar
# Output: build/libs/ecibot-server-1.0.jar
```

## Common Build Commands

| Command | Purpose |
|---------|---------|
| `./gradlew build` | Full build + tests |
| `./gradlew test` | Run tests only |
| `./gradlew fatJar` | Build executable fat JAR |
| `./gradlew sonar` | Run SonarCloud analysis |
| `./gradlew build sonar --info` | CI build + analysis |

## Blockers (MUST NOT)
- Adding dependencies directly in `build.gradle.kts` — always add to version catalog first
- Changing ktlint reporter from `github-pr-review` (required for PR annotations)
- Removing `fetch-depth: 0` from SonarCloud workflow
- Modifying Sonar exclusions without review
- Hardcoding secrets in workflow files — always use GitHub Secrets/Vars

## References
- `.github/workflows/ktlint.yaml` — ktlint CI
- `.github/workflows/sonarcloud-analysis.yaml` — SonarCloud CI
- `build.gradle.kts` — build configuration, JaCoCo, Sonar, fat JAR
- `gradle/libs.versions.toml` — version catalog
