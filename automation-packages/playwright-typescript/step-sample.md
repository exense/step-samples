---
practice: [e2e-test-automation, load-testing]
framework: playwright
language: typescript
technology: web
strategy: keyword-driven
---

# Playwright Keywords in TypeScript

Implements Step keywords in TypeScript using the Playwright API and the Step Node.js SDK. The same keywords are used in two plans: a functional test case and a load test, showing how a single keyword library serves multiple purposes.

## What this sample shows

- Writing Step keywords in TypeScript compiled to JavaScript for the Step `Node` keyword runtime
- Sharing one keyword library across a functional test plan and a load test plan
- Using Jest as the local unit test runner for keywords (with the `step-node-agent` runner pattern)
- Managing keyword compilation with `tsconfig` and running tests via `jest.config.js`

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: two plans (functional + load) and two `Node` keyword declarations |
| `nodejs-keywords/keywords/keywords-typescript.ts` | TypeScript source: `findProduct` and `checkout` keywords using Playwright |
| `nodejs-keywords/jest.config.js` | Jest configuration for local keyword unit tests |
| `nodejs-keywords/jest.setup.js` | Step Node agent setup for running keywords in Jest |