---
practice: production-monitoring
framework: cypress
language: javascript
technology: web
strategy: project-integration
---

# Synthetic Monitoring with Cypress

Runs a Cypress test on a cron schedule to continuously verify that a production web application is available and responsive. Fails the check if execution time exceeds the defined SLA.

## What this sample shows

- Scheduling automated checks with a built-in `schedules` cron entry in `automation-package.yaml`
- Wrapping a Cypress spec as a Step keyword using the `Cypress` keyword type (no custom code)
- Using `performanceAssert` to enforce a response-time SLA (execution < 60 seconds)
- Reusing an existing Cypress project as a monitoring probe without modification

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: Cypress keyword, monitoring plan, and cron schedule |
| `cypress-test/cypress/e2e/opencart.cy.js` | Cypress spec: simulated user visit to OpenCart |
| `cypress-test/cypress.config.js` | Cypress configuration |