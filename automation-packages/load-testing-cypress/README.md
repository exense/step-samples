---
use-case: load-testing
framework: cypress
language: javascript
target-platform: web
approach: project-integration
level: beginner
---

# Load Testing with Cypress

Runs an existing Cypress test suite as a Step keyword and distributes its execution across multiple agents via a Thread Group, turning a functional Cypress spec into a load test.

## What this sample shows

- Wrapping a Cypress project as a Step keyword using the `Cypress` keyword type in `automation-package.yaml` (no custom code required)
- Distributing Cypress test execution across 10 agents with 10 iterations each (100 total runs)
- Auto-provisioning agents with `agents: auto_detect`
- Reusing an existing Cypress project structure without modification

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: Cypress keyword and Thread Group load plan |
| `cypress-test/cypress/e2e/opencart.cy.js` | Cypress spec: typical OpenCart browsing and purchase journey |
| `cypress-test/cypress.config.js` | Cypress configuration |
| `cypress-test/package.json` | Node.js dependencies for the Cypress project |
