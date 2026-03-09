# Playwright Test Runner Sample

This sample demonstrates running Playwright tests using the Playwright Test Runner inside a [Step](https://step.exense.ch) automation package using the `ExecuteBash` keyword from the Step system library.

## Prerequisites

- **Step** instance (cloud or on-premise)
- **Step CLI** (https://step.dev/knowledgebase/devops/automation-package-cli/step-cli)
- **curl** or **wget** (to download the dependency)

## Setup

Before building or uploading the automation package, download the required Step system keyword library:

```bash
bash download-dependencies.sh
```

This downloads `step-library-kw-system-1.0.30.jar` from Maven Central into the project directory. The script is idempotent — running it again when the file already exists is a no-op.

> **Manual download:** If you prefer, download the JAR directly from:
> https://repo1.maven.org/maven2/ch/exense/step/library/step-library-kw-system/1.0.30/step-library-kw-system-1.0.30.jar
> and place it in this directory.

## Project structure

```
playwright-typescript-generic/
├── automation-package.yaml       # Step automation package descriptor
├── download-dependencies.sh      # Dependency download script
├── package.json                  # Node.js / Playwright dependencies
├── playwright.config.ts          # Playwright configuration
├── tests/
│   └── example.spec.ts           # Sample Playwright tests
└── step-library-kw-system-1.0.30.jar  # Downloaded at setup (not committed)
```

## What the automation package does

The plan defined in `automation-package.yaml` executes three steps on the Step agent:

1. `npm ci` — installs Node.js dependencies from `package-lock.json`
2. `npx playwright install --with-deps` — installs Playwright browsers and OS dependencies
3. `npx playwright test` — runs all tests under the `tests/` directory

The `ExecuteBash` keyword (provided by `step-library-kw-system-1.0.30.jar`) is used to run each shell command on the agent.

## Uploading to Step

Once the JAR is present, upload the package through the Step UI or CLI:

```bash
# Example using the Step CLI (adjust flags for your environment)
step ap execute --stepUrl=https://<yourCluster>.stepcloud.ch --token=<your API key>
```