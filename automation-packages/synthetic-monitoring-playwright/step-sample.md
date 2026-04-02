---
use-case: monitoring
framework: playwright
language: java
target-platform: web
approach: keyword-driven
level: intermediate
---

# Synthetic Monitoring with Playwright (Java)

Continuously monitors a production web application by running a Playwright keyword on a cron schedule. Detects regressions by asserting that the end-to-end flow completes within the defined SLA.

## What this sample shows

- Scheduling automated monitoring probes with the `schedules` section in `automation-package.yaml`
- Reusing a Playwright keyword (purchase flow) both as a functional check and a latency probe
- Using `performanceAssert` to enforce a response-time SLA (keyword execution < 5 seconds)
- Running the package locally with JUnit before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Package definition: monitoring plan, performance assertion, and cron schedule |
| `src/main/java/.../PlaywrightKeywords.java` | Playwright keyword: full OpenCart purchase journey |
