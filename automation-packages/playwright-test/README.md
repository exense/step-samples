---
use-case: e2e-testing
framework: playwright
language: javascript
target-platform: web
approach: code-first
level: intermediate
---

# Playwright Test Runner Integration

Runs a standard `@playwright/test` test suite on a Step agent by invoking it through CLI commands (`npm ci`, `npx playwright install`, `npx playwright test`). This lets teams bring an existing Playwright Test project to Step without rewriting it as keywords.

## What this sample shows

- Executing an unmodified `@playwright/test` project on a Step agent using the `ExecuteBash` keyword
- Collecting the `playwright-report` HTML report as a Step artifact, making it visible in the execution results
- Using the `GeneralScript` / `ExecuteBash` keyword from the Step system library (downloaded via `download-dependencies.sh`)
- Auto-provisioning agents with `agents: auto_detect`

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Plan: sequential bash steps (install deps → install browsers → run tests) |
| `download-dependencies.sh` | Downloads the Step system library jar containing `ExecuteBash` |
| `package.json` | Playwright Test project dependencies |
| `playwright.config.ts` | Playwright configuration |
| `tests/` | Playwright Test spec files |

---

## Setup

Before building or uploading the automation package, download the required Step system keyword library:

```bash
bash download-dependencies.sh
```

This downloads `step-library-kw-system-1.0.30.jar` from Maven Central into the project directory. The script is idempotent — running it again when the file already exists is a no-op.

> **Manual download:** If you prefer, download the JAR directly from Maven Central and place it in this directory.

## How it works

The plan defined in `automation-package.yaml` executes three steps on the Step agent:

1. `npm ci` — installs Node.js dependencies from `package-lock.json`
2. `npx playwright install --with-deps` — installs Playwright browsers and OS dependencies
3. `npx playwright test` — runs all tests under the `tests/` directory

The `ExecuteBash` keyword (provided by `step-library-kw-system-1.0.30.jar`) runs each shell command on the agent. The `playwright-report` folder is declared as an artifact so Step collects and exposes it in the execution results.

## Executing on Step

Once the JAR is present, execute the package through the Step CLI:

```bash
step ap execute --stepUrl=https://<yourCluster>.stepcloud.ch --token=<your API key> --projectName=Common
```
