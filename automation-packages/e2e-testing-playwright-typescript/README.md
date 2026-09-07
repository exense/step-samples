---
use-case: e2e-testing
framework: playwright
language: typescript
target-platform: web
approach: keyword-driven
level: intermediate
---

# E2E Testing with Playwright (TypeScript)

Demonstrates end-to-end test automation against the OpenCart demo shop using Playwright keywords written in TypeScript, structured as a Step Automation Package with a proper test set / test case hierarchy.

This is the TypeScript counterpart of [e2e-testing-playwright](../e2e-testing-playwright/) (Java) and [e2e-testing-playwright-dotnet](../e2e-testing-playwright-dotnet/) (C#). The three run the same test case: buy a MacBook in OpenCart, then verify the order confirmation mail in a webmail client.

## What this sample shows

- Writing Step keywords in TypeScript compiled to JavaScript for the Step `Node` keyword runtime
- Shipping the TypeScript sources and letting the agent compile them, so no build output is committed or uploaded
- Structuring a test plan with `testSet` and `testCase` nodes
- Cross-application testing: purchasing a product in OpenCart and verifying the order confirmation email in a webmail UI
- Keeping plan-facing keyword names (`Purchase product in OpenCart`) while the TypeScript sources use ordinary function names
- Separating business data (keyword `inputs`) from technical configuration (`properties`, declared as package `parameters` and overridable as Step Parameters)
- Attaching a Playwright trace to the execution report, including when the keyword fails
- Unit-testing the keywords locally with the built-in `node:test` runner and the `step-node-agent` runner

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: plan, parameters, and the two `Node` keyword declarations |
| `nodejs-keywords/src/opencart-playwright-keywords.ts` | Keyword: add a product to the cart and complete the guest checkout |
| `nodejs-keywords/src/webmail-playwright-keywords.ts` | Keyword: read and verify the order confirmation email |
| `nodejs-keywords/tests/e2e-testing-playwright.test.ts` | Local run of the test case, with `node:test` + `tsx` |
| `nodejs-keywords/tsconfig.json` | Keyword build: compiles `src/` to `keywords/`, which the Step `Node` runtime loads |
| `nodejs-keywords/tsconfig.test.json` | Type-checks the tests, which are not part of the keyword build |
| `.apignore` | Ships `src/` and excludes the build output — the agent compiles during `npm install` |

## Running locally

```bash
cd nodejs-keywords
npm install
npm test          # builds, type-checks, then runs the test case against the live demo apps
```

The keywords are compiled to `keywords/` during `npm install` — that is the directory the Step `Node` keyword runtime loads.

## Deploy to Step

```bash
step ap execute .
```

## Notes

- **Built on the agent, not before upload.** The package ships TypeScript sources and no compiled output: `.apignore` excludes `keywords/`, and `package.json` declares

  ```json
  "prepare": "npm run build && playwright install chromium"
  ```

  The Step Node agent runs a plain `npm install` in the uploaded package before the first keyword call, and that is what triggers `prepare` — so deploying needs no local build step, and a stale `keywords/` can never be shipped by accident.

- **Keyword names with spaces.** The Step Node runtime resolves a keyword by looking the declared `name` up as a property of the compiled modules, so it is not restricted to valid JavaScript identifiers. Each source file therefore ends with an aliased export:

  ```ts
  export { purchaseProductInOpenCart as 'Purchase product in OpenCart' };
  ```

  That keeps the plan identical to the Java and .NET versions of this sample while the TypeScript code stays idiomatic.

- **A browser per keyword.** Unlike [playwright-typescript](../playwright-typescript/), where two keywords share one browser through the Step session, each keyword here owns its browser and closes it in a `finally`.

- **Untyped keyword arguments.** `step-node-agent` does not ship TypeScript definitions yet, so `input`, `output`, `session` and `properties` are implicitly `any` (`noImplicitAny` is off). The keyword contract is documented by the parameter list itself.

- **CommonJS, not ESM.** The project must stay CommonJS (no `"type": "module"` in `package.json`). The Step Node agent copies CommonJS helper files into the keyword project and forks them, so switching the project to ESM breaks the runtime. `tsconfig` therefore uses `"module": "node20"`, which keeps the CommonJS emit while enabling modern module resolution.

- **Headless.** `headless` is a *property*, not an input: it is declared once in the `parameters:` block of `automation-package.yaml`. Set it to `"false"` there — or override it as a Parameter in Step, per project or environment — to watch the browser.