---
use-case: load-testing
framework: okhttp
language: java
target-platform: api
approach: keyword-driven
level: intermediate
---

# Load Testing HTTP with OkHttp (Java)

Demonstrates how to load test an HTTP/REST API from Step using the OkHttp Java client. Each keyword encapsulates one step of a multi-step purchase flow (home page, add to cart, checkout).

## What this sample shows

- Writing fine-grained Step keywords in Java using the OkHttp client
- Decomposing a user journey into individual reusable keywords (`OpenCart Home`, `OpenCart Add MacBook`, `OpenCart Checkout`)
- Sequencing keywords within a Thread Group to simulate a realistic user flow
- Unit-testing keywords locally with JUnit before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Package definition: Thread Group plan with sequenced keyword calls |
| `src/main/java/.../OkHttpKeywords.java` | Step keywords wrapping OkHttp requests |
| `src/test/java/.../OkHttpKeywordTest.java` | JUnit unit tests for individual keywords |
| `src/test/java/.../RunAutomationPackageTest.java` | JUnit runner for the full automation package |
