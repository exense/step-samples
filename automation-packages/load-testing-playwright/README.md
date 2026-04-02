---
use-case: load-testing
framework: playwright
language: java
target-platform: web
approach: keyword-driven
level: intermediate
---

# Load Testing with Playwright (Java)

Uses Playwright Java keywords to simulate real browser-based user journeys under load. The Step Thread Group distributes the execution across auto-provisioned agents, producing a realistic browser-driven load test.

## What this sample shows

- Writing a Step keyword in Java that drives a full browser session with Playwright
- Scaling browser-based load across multiple Step agents (10 VUs × 100 iterations)
- Auto-provisioning agents with `agents: auto_detect`
- Running the keyword locally with JUnit before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Package definition: Thread Group plan |
| `src/main/java/.../PlaywrightKeywords.java` | Keyword: browser-based OpenCart purchase flow |
