---
use-case: [e2e-testing, load-testing]
framework: playwright
language: typescript
target-platform: web
approach: keyword-driven
level: intermediate
---

# Playwright Keywords in TypeScript

Implements Step keywords in TypeScript using the Playwright API and the Step Node.js SDK. The same keywords are used in two plans: a functional test case and a load test, showing how a single keyword library serves multiple purposes.

## What this sample shows

- Writing Step keywords in TypeScript compiled to JavaScript for the Step `Node` keyword runtime
- Sharing one keyword library across a functional test plan and a load test plan
- Separating business data (keyword `inputs`) from technical configuration (`properties`, declared as package `parameters` and overridable as Step Parameters)
- Sharing a Playwright browser between keywords through the Step session, which disposes it automatically
- Attaching a Playwright trace to the execution report, including when the keyword fails
- Unit-testing keywords locally with the built-in `node:test` runner and the `step-node-agent` runner

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: two plans (functional + load) and two `Node` keyword declarations |
| `nodejs-keywords/src/keywords-typescript.ts` | TypeScript source: `findProduct` and `checkout` keywords using Playwright |
| `nodejs-keywords/tests/keywords-typescript.test.ts` | Local unit test of the two keywords, run with `node:test` + `tsx` |
| `nodejs-keywords/tsconfig.json` | Keyword build: compiles `src/` to `keywords/`, which the Step `Node` runtime loads |
| `nodejs-keywords/tsconfig.test.json` | Type-checks the tests, which are not part of the keyword build |

## Running locally

```bash
cd nodejs-keywords
npm install
npm test          # builds, type-checks, then runs the keywords against the demo store
```

The keywords are compiled to `keywords/` — that is the directory the Step `Node` keyword runtime loads, and it must be built before the automation package is deployed.

## Notes

- **Untyped keyword arguments.** `step-node-agent` does not ship TypeScript definitions yet, so `input`, `output`, `session` and `properties` are implicitly `any` (`noImplicitAny` is off). This keeps the sample focused on Playwright rather than on hand-written SDK declarations; the keyword contract is documented by the parameter list itself.
- **CommonJS, not ESM.** The project must stay CommonJS (no `"type": "module"` in `package.json`). The Step Node agent copies CommonJS helper files into the keyword project and forks them, so switching the project to ESM breaks the runtime. `tsconfig` therefore uses `"module": "node20"`, which keeps the CommonJS emit while enabling modern module resolution.
- **Headless.** `headless` is a *property*, not an input: it is declared once in the `parameters:` block of `automation-package.yaml` and applies to both plans. Set it to `"false"` there — or override it as a Parameter in Step, per project or environment — to watch the browser. Step agents support headed and headless. Properties always arrive as strings, hence the explicit `properties['headless'] === 'true'` comparison; a truthiness check would read `"false"` as true.