---
practice: e2e-test-automation
framework: playwright
language: java
technology: web
strategy: keyword-driven
---

# E2E Testing with Playwright — Data-Driven (Java)

Extends the basic Playwright E2E sample with a data-driven approach: test cases are parameterized from a CSV file, so the same purchase flow runs once per customer record.

## What this sample shows

- Driving test cases from an external CSV data source using the `forEach` / `csv` plan node
- Passing CSV row values as keyword inputs via expressions (`row.firstname`, `row.lastname`)
- Asserting on keyword output values (email subject contains expected order reference)
- Combining data-driven iteration with functional assertions in `automation-package.yaml`

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Plan with `forEach` CSV loop, keyword calls, and assertions |
| `src/main/resources/testdata/customers.csv` | Input data: one row per customer to test |