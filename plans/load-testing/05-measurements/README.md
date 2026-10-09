---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: advanced
---

# 05 — Measurements

Everything a load test reports comes out of measurements. Which ones exist, what they are called
and how they nest is decided partly by Step and partly by the plan.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml)** and the
`Browse Catalog` keyword. This page holds the plan index and the reference tables.

## The plans

| Plan | Shows |
|------|-------|
| A — Where measurements come from | The three sources side by side, and which ones carry an SLA |

It is expected to pass.

## The three sources

| Source | Named after | Created by | Can carry a `performanceAssert` |
|--------|-------------|-----------|-------------------------------|
| Keyword call | the **keyword** (never the node) | Step, automatically, for every call | **yes** |
| Instrumented node | the node's `nodeName` | `instrumentNode: true` | **no** — dashboards only |
| Custom | whatever the keyword chooses | `output.startMeasure(...)` in the keyword | **yes** |

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | One plan on where measurements come from and which ones carry an SLA |
| `keywords/browseCatalog.groovy` | Two custom measurements inside one keyword call |
| `keywords/checkout.groovy` | A plain keyword — its measurement is created for it |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
