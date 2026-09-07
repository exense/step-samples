# Step Automation Package Samples

This directory contains ready-to-run [Step](https://step.dev) Automation Package samples covering the most common automation use cases: load testing, end-to-end testing, synthetic monitoring, and RPA.

Each sample is a self-contained project with its own `README.md` that includes a structured frontmatter block for quick filtering and discovery.

---

## How to read the frontmatter

Every sample README starts with a YAML frontmatter block:

```yaml
---
use-case: load-testing
framework: playwright
language: java
target-platform: web
approach: keyword-driven
level: intermediate
---
```

### Field definitions

| Field | Description                                          | Possible values |
|-------|------------------------------------------------------|-----------------|
| `use-case` | The testing practice this sample addresses           | `load-testing`, `e2e-testing`, `monitoring`, `rpa` |
| `framework` | The test framework or library used                   | `playwright`, `selenium`, `cypress`, `appium`, `jmeter`, `k6`, `grpc`, `okhttp`, `serenity-bdd`, `oryon`, `step-library-http`, … |
| `language` | The programming language of the keyword or test code | `java`, `typescript`, `javascript`, `csharp`, `groovy`, `xml` |
| `target-platform` | What the automation targets                          | `web`, `api`, `mobile`, `desktop`, `syrius` |
| `approach` | How the test logic is integrated with Step           | `keyword-driven`, `project-integration`, `code-first` |
| `level` | Estimated complexity for someone new to Step         | `beginner`, `intermediate`, `advanced` |

### Approach values explained

- **`keyword-driven`** — Test logic is written as Step Keywords (Java, TypeScript, etc.) and orchestrated from a Step plan. This is the most flexible approach.
- **`project-integration`** — An existing test project (Cypress spec, JMeter `.jmx`, Serenity suite…) is plugged into Step as-is using a built-in keyword type. No custom keyword code is required.
- **`code-first`** — The test suite is a standalone project invoked via a CLI command (`npx playwright test`, `mvn test`, `pytest`, …). Step runs it on an agent using the `ExecuteBash` keyword and collects the results.

---

## Sample index

### Load Testing

| Sample | Framework | Language | Approach | Level |
|--------|-----------|----------|----------|-------|
| [load-testing-http](load-testing-http/) | step-library-http | java | keyword-driven | beginner |
| [load-testing-jmeter](load-testing-jmeter/) | jmeter | xml | project-integration | beginner |
| [load-testing-cypress](load-testing-cypress/) | cypress | javascript | project-integration | beginner |
| [load-testing-okhttp](load-testing-okhttp/) | okhttp | java | keyword-driven | intermediate |
| [load-testing-playwright](load-testing-playwright/) | playwright | java | keyword-driven | intermediate |
| [load-testing-selenium](load-testing-selenium/) | selenium | java | keyword-driven | intermediate |
| [load-testing-k6](load-testing-k6/) | k6 | javascript | project-integration | intermediate |
| [load-testing-grpc](load-testing-grpc/) | grpc | java | keyword-driven | advanced |
| [load-testing-serenity](load-testing-serenity/) | serenity-bdd | java | project-integration | advanced |
| [load-testing-appium](load-testing-appium/) | appium | java | keyword-driven | advanced |

### E2E Testing

| Sample | Framework | Language | Approach | Level |
|--------|-----------|----------|----------|-------|
| [e2e-testing-playwright](e2e-testing-playwright/) | playwright | java | keyword-driven | intermediate |
| [e2e-testing-playwright-data-driven](e2e-testing-playwright-data-driven/) | playwright | java | keyword-driven | intermediate |
| [e2e-testing-playwright-dotnet](e2e-testing-playwright-dotnet/) | playwright | csharp | keyword-driven | intermediate |
| [e2e-testing-playwright-typescript](e2e-testing-playwright-typescript/) | playwright | typescript | keyword-driven | intermediate |
| [playwright-typescript](playwright-typescript/) | playwright | typescript | keyword-driven | intermediate |
| [playwright-test](playwright-test/) | playwright | javascript | code-first | intermediate |
| [playwright-automation-packages-with-library](playwright-automation-packages-with-library/) | playwright | java | keyword-driven | advanced |

### Monitoring

| Sample | Framework | Language | Approach | Level |
|--------|-----------|----------|----------|-------|
| [synthetic-monitoring-cypress](synthetic-monitoring-cypress/) | cypress | javascript | project-integration | beginner |
| [synthetic-monitoring-playwright](synthetic-monitoring-playwright/) | playwright | java | keyword-driven | intermediate |
| [synthetic-monitoring-playwright-advanced](synthetic-monitoring-playwright-advanced/) | playwright | java | keyword-driven | advanced |

### RPA

| Sample | Framework | Language | Approach | Level |
|--------|-----------|----------|----------|-------|
| [rpa-selenium](rpa-selenium/) | selenium | java | keyword-driven | intermediate |

### Reference

| Sample | Description | Level |
|--------|-------------|-------|
| [standard-format](standard-format/) | One package demonstrating every keyword type (JMeter, .NET, Node.js, k6, Oryon) | intermediate |
| [oryon](oryon/) | Minimal Oryon/Groovy keyword sample | beginner |

---

## Choosing an approach

```
Do you have an existing test project (Cypress, JMeter, Playwright Test, Serenity…)?
│
├── Yes, I want to run it as-is via CLI  ──────────────────► code-first
│     (npm test, npx playwright test, mvn test…)              e.g. playwright-test
│
├── Yes, I want Step to distribute it across agents  ────────► project-integration
│     (wrap the project file as a keyword)                     e.g. load-testing-cypress
│                                                              load-testing-jmeter
│
└── No, I want full control / custom logic  ─────────────────► keyword-driven
      (write keywords in Java, TypeScript, etc.)               e.g. load-testing-playwright
                                                               e2e-testing-playwright
```
