---
use-case: monitoring
framework: playwright
language: java
target-platform: web
approach: keyword-driven
level: advanced
---

# Synthetic Monitoring with Playwright — Advanced (Java)

Extends the basic synthetic monitoring sample with enterprise-grade features: assertion plans that validate schedule health over a rolling time window, and alerting rules that send email notifications when an incident is opened.

## What this sample shows

- Scheduling automated monitoring probes with `schedules` and a cron expression
- Using `assertionPlan` to assert that scheduled executions did not fail more than N times in a rolling window
- Configuring `alertingRules` to send email notifications on `IncidentOpenedEvent`
- Binding alerting conditions to a specific schedule by name using `BindingCondition`
- Chaining a monitoring plan with an assertion plan via `assertionPlanName`

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Full config: monitoring plan, assertion plan, schedule, and alerting rule |
| `src/main/java/.../PlaywrightKeywords.java` | Playwright keyword: full OpenCart purchase journey |
