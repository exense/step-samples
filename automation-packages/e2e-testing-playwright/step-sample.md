---
practice: e2e-test-automation
framework: playwright
language: java
technology: web
strategy: keyword-driven
---

# E2E Testing with Playwright (Java)

Demonstrates end-to-end test automation against the OpenCart demo shop using Playwright keywords written in Java, structured as a Step Automation Package with a proper test set / test case hierarchy.

## What this sample shows

- Writing Step keywords in Java using the Playwright Java API
- Structuring a test plan with `testSet` and `testCase` nodes
- Cross-application testing: purchasing a product in OpenCart and verifying the order confirmation email in a webmail UI
- Running the automation package locally with JUnit before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Package definition: plan, keywords, and test structure |
| `src/main/java/.../OpenCartPlaywrightKeywords.java` | Keyword: add MacBook to cart and complete purchase |
| `src/main/java/.../WebmailPlaywrightKeywords.java` | Keyword: read and verify the order confirmation email |
| `src/test/java/.../AutomationPackageTest.java` | JUnit runner for local execution |