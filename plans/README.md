# Step plan samples

This directory is about one thing: **how to write a Step plan**.

A **plan** is the implementation of an automation scenario — a functional test case, a load
test, an RPA routine, a synthetic monitoring probe. It combines **keywords**, the building
blocks that do the work, with **controls** that build up the execution logic: loops,
branches, retries, waits.

Every sample here ships as a runnable automation package, so each plan can be validated and
executed rather than just read. Keyword code is deliberately reduced to 3-line stubs so the
plan itself is the subject.

### How this differs from `automation-packages/`

[automation-packages/](../automation-packages/) holds **complete real-world blueprints** —
a full project for a given use case and stack (load testing with Playwright/TypeScript,
synthetic monitoring with Cypress, RPA with Selenium), including its build, its keywords and
its plans. That is where you go to start a project.

This directory is the **plan-authoring reference**: one plan concept per sample, stripped of
everything else. That is where you go while writing a plan.

## Samples by use case

| Use case | Samples | Status |
|----------|---------|--------|
| [**RPA**](rpa/) | 7 samples — loops, branching, resilience, sessions, scheduling, reuse | available |
| [**Load testing**](load-testing/) | 6 samples — thread groups, scenarios, data sets, measurements, SLA gates | available |
| Functional testing | — | planned |
| Monitoring | — | planned |

## Reference

For what each control does and how to configure it, see the official
[controls documentation](https://step.dev/knowledgebase/userdocs/plans/controls/).

[reference/](reference/) holds small standalone YAML plans illustrating the syntax:

| File | Shows |
|------|-------|
| [reference/basic-plan-syntax.yml](reference/basic-plan-syntax.yml) | The shape of a plan: root artefact, `callKeyword` with inputs, capturing an output, `if`, `assert`, `check` |
| [reference/dynamic-values.yml](reference/dynamic-values.yml) | Static values vs `expression:`, where plan variables come from, dynamic keyword names and `routing` |
| [reference/performance-assert.yml](reference/performance-assert.yml) | A `threadGroup` with a `performanceAssert` — the load-testing shape, and the `after`-block rule |

## Plan formats

Step has three plan formats:

| Format | Written as | Used by these samples |
|--------|-----------|-----------------------|
| **YAML** | The tree of controls documented at [step.dev](https://step.dev/knowledgebase/userdocs/plans/controls/) | Yes — the whole `rpa/` and `load-testing/` sets |
| **Plain text** | A compact line-based syntax, one keyword call per line | No |
| **UI** | Built in the Step plan editor; [imported and exported](https://step.dev/knowledgebase/userdocs/import-export-entities/) as JSON | No — see [legacy-exports/](legacy-exports/) for what an export looks like |

**Automation packages support YAML and plain text.** Editing an automation package's plans in
the UI is planned but not currently supported.

### Where a YAML plan lives

Inside an automation package, a YAML plan can be declared either way:

```yaml
plans:                        # directly in the main descriptor
  - name: "My plan"
    root:
      testCase:
        children: []

fragments:                    # or pulled in from a fragment file
  - "plans/my-plan.yml"
```

A **standalone YAML plan** — a file with a top-level `root:`, like the three in
[reference/](reference/) — is not a separate format. It is the same tree, and it can be
either incorporated into an automation package like any other plan, or created centrally in
the Step UI with **Add plan → Create from YAML**.

Plain-text plans are declared with `plansPlainText`, each entry naming a `file`, a `name` and
a `rootType`:

```yaml
plansPlainText:
  - name: "Open the site"
    file: "plans/open-site.plan"
    rootType: TestCase
```

## Schema

All YAML here targets Automation Package schema **1.2.0**. Any Step instance serves its own
schema at:

```
<your-step-instance>/rest/automation-packages/schema
```

Point your IDE at it to get completion and validation while editing
`automation-package.yaml`.

## Frontmatter

Each sample README carries the same descriptor block used across this repository (see
[automation-packages/README.md](../automation-packages/README.md)), plus `focus: plans` to
mark it as plan-authoring material rather than a technology sample:

```yaml
---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: beginner
---
```

## Running a sample

```bash
step ap execute -p <sample-dir> -u <your-step-url> --token <your-token> --projectName <your-project>
```

`--includePlans` runs a subset. It is comma-separated, so plan names containing a comma
cannot be selected individually — worth avoiding when naming plans.

`execute` runs the plans and nothing else. To register a package in a project — its plans,
keywords, **schedules** and parameters — deploy it:

```bash
step ap deploy -p <sample-dir> -u <your-step-url> --token <your-token> --projectName <your-project>
```
