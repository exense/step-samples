---
practice: e2e-test-automation
framework: playwright
language: java
technology: web
strategy: keyword-driven
---

# Playwright Automation Packages with Shared Library

A multi-module Maven project that shows how to share a common Playwright driver library across multiple independent Automation Packages. Keywords from different packages reuse the same browser instance within a Step session.

## What this sample shows

- Structuring a multi-module Maven project with a parent BOM for dependency management
- Building a shared `AbstractPlaywrightKeyword` library as an uber-jar deployed independently to Step
- Sharing a Playwright browser instance across keywords from different packages using Step session storage (`session.put()` / `session.get()`)
- Packaging consumer packages as uber-jars that exclude the shared library (`scope: provided`)
- Running integration tests locally with JUnit and deploying all packages together to a Step instance

## Modules

| Module | Purpose |
|--------|---------|
| `playwright-automation-package-library` | Shared uber-jar: `AbstractPlaywrightKeyword` with driver lifecycle management |
| `playwright-automation-package-opencart` | Keywords for the OpenCart demo shop |
| `playwright-automation-package-exense-website` | Keywords for the Exense website |
| `playwright-automation-package-test` | Integration test: plan that calls keywords from both packages in one session |