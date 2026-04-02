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
| `tests/` | Playwright Test spec files |
