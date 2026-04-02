---
use-case: load-testing
framework: step-library-http
language: java
target-platform: api
approach: keyword-driven
level: beginner
---

# Load Testing HTTP / REST APIs

Uses Step's built-in `HttpRequest` keyword to load test a REST API without writing any custom code. The plan defines a realistic multi-step purchase flow (add to cart → checkout) with inline assertions and performance thresholds, entirely in `automation-package.yaml`.

## What this sample shows

- Calling HTTP endpoints directly from `automation-package.yaml` using the built-in `HttpRequest` keyword
- Composing multi-step request sequences with named `sequence` nodes for measurement
- Asserting HTTP response bodies inline using the `assert` plan node
- Validating SLA compliance with `performanceAssert` (average response time thresholds)
- Running 10 concurrent virtual users with 10 iterations each

## Key files

| File | Purpose |
|------|---------|
| `src/main/resources/automation-package.yaml` | Full plan: sequences, HTTP calls, assertions, and performance assertions |
| `src/test/java/.../RunAutomationPackageTest.java` | JUnit runner for local execution |
