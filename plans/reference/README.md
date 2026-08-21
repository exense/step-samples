# Plan syntax reference

Small, self-contained YAML plans illustrating the syntax, independent of any use case.

For the controls themselves — what each one does and how to configure it — see the official
documentation:
**[step.dev/knowledgebase/userdocs/plans/controls](https://step.dev/knowledgebase/userdocs/plans/controls/)**.

| File | Shows |
|------|-------|
| [basic-plan-syntax.yml](basic-plan-syntax.yml) | The shape of a plan: a root artefact, `callKeyword` with inputs, capturing an output with a nested `set`, `if`, `assert` and `check` |
| [dynamic-values.yml](dynamic-values.yml) | Static values vs `expression:`, where plan variables come from, and dynamic keyword names and `routing` |
| [performance-assert.yml](performance-assert.yml) | A `threadGroup` with a `performanceAssert` — the load-testing shape, and the `after`-block rule |

These files are **syntax illustrations, not runnable plans**: the keywords they call do not
exist. For plans that execute and assert their own outcome, see [../rpa/](../rpa/) and
[../load-testing/](../load-testing/).

## Standalone YAML plan files

Each file here is a **standalone plan**: a top-level `root:` with no package around it. This
is not a separate plan format — it is the same YAML tree, just not nested inside a
descriptor.

Such a file can be used two ways:

- **Straight into the Step UI** — **Add plan → Create from YAML**, which creates the plan
  centrally from the YAML.
- **Inside an automation package** — incorporated like any other plan.

```yaml
version: 1.0.0
name: "Basic plan syntax"
root:
  testCase:
    children: []
```

Inside an `automation-package.yaml` the same tree sits one level deeper, under a named entry
in `plans:`:

```yaml
version: "1.2.0"
name: "my-package"
plans:
  - name: "Basic plan syntax"
    root:
      testCase:
        children: []
```

The node syntax is identical either way. See the [plans README](../README.md) for how the
three plan formats relate.
