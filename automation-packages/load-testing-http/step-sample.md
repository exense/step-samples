---
practice: load-testing
framework: step-library-http
language: java
technology: rest
strategy: keyword-driven
---

# Load Testing HTTP / REST APIs

Uses Step's `HttpRequest` keyword from the official HTTP keyword library to load test a REST API without writing any custom Java code. The plan defines a realistic multi-step purchase flow (add to cart → checkout) with inline assertions and performance thresholds.

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