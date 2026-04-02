---
use-case: load-testing
framework: selenium
language: java
target-platform: web
approach: keyword-driven
level: intermediate
---

# Load Testing with Selenium (Java)

Uses Selenium WebDriver keywords written in Java to simulate real browser-based user journeys under load. The Step Thread Group distributes execution across auto-provisioned agents.

## What this sample shows

- Writing a Step keyword in Java using Selenium WebDriver
- Scaling browser-based load across multiple Step agents (10 VUs × 100 iterations)
- Auto-provisioning agents with `agents: auto_detect`
- Structuring a Maven project for a Step Automation Package with a shaded jar build

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Package definition: Thread Group plan |
| `src/main/java/.../SeleniumKeywords.java` | Keyword: browser-based OpenCart purchase flow |
