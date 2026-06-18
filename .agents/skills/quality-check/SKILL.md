---
name: quality-check
description: "CRITICAL: Load BEFORE opening any PR. Missing this = failing gates and rejected PRs. Validates build, lint, tests. Pre-PR only."
---

## When to use me
- At the end of a task before opening a PR
- During PR review to validate locally

## Not intended for
- Day-to-day coding → use project-specific skills
- Code review → use `code-review`

---

## Quality Gates (MUST)

| Gate | Command | Status |
|------|---------|--------|
| Build | `./gradlew build` | Must pass |
| Lint | No linter configured | N/A |
| Tests | `./gradlew test` | Must pass |

## Step 1 — Build
```bash
./gradlew build
```

## Step 2 — Tests
```bash
./gradlew test
```

## Reporting
- **BLOCKER**: Failing build, failing tests
- **WARNING**: Non-blocking improvements
