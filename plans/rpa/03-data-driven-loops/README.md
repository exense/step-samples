---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 03 — Data-driven loops

The bread and butter of RPA: *do this for every row*. Five plans in one package, each
showing a different loop or data source, all doing the same "process each record" job.

## What this sample shows

- `forEach` over a data source — the main RPA loop
- Reading cells with `row.<ColumnName>`, and renaming `row` with `item`
- Parallel bot workers with `threads`, and failure tolerance with `maxFailedLoops`
- Five of the nine data sources: `csv`, `json-array`, `sequence`, plus documented `folder`
  and `sql`
- `for` — a plain counter loop
- **Writing results back into the source row** with `script` + `row.put(...)`

## The plans

| Plan | Shows |
|------|-------|
| A | `forEach` + `csv`, `row.<Column>` expressions |
| B | `item` to rename the row variable, `threads: 2`, `maxFailedLoops` |
| C | `json-array` and `sequence` sources; `folder` documented |
| D | `for` with `start` / `end` / `inc` |
| E | Write-back with `script` + `row.put(...)` |

## Two things worth knowing before you copy this

### `folder` is not a package resource

`csv`, `excel` and `file` take a `file:` which is a **resource reference** — a
package-relative path, so the data travels with the package. `folder` takes a plain
**string path resolved on the agent's own filesystem**. It cannot point inside the package;
doing so fails with a `NullPointerException` in `step.datapool.file.FileDataPoolImpl`.

That is right for a real drop-folder bot — the folder is normally a network share — but it
means the `folder` example in plan C is documented rather than executed.

### Where write-back actually lands

`row.put("Result", "OK")` inside a `script` control mutates the current row, and the new
value is readable immediately afterwards in the same iteration. Plan E executes and asserts
exactly that.

But a data file **bundled in the automation package is not a persistent store**. With both a
packaged CSV and a packaged XLSX, a second `forEach` over the same file in the same
execution still reads the original values.

To persist, point the data source at a **Step-managed resource** by id:

```yaml
dataSource:
  csv:
    file:
      id: "<your-resource-id>"    # a resource uploaded to Step
```

…or the same with `excel:` plus `headers: true`.

The write reaches the resource and survives the run, so a later execution reads the updated
values. Upload your file as a resource in Step and substitute its id — which is also why
this sample cannot ship a runnable version of it, since the id only exists on the instance
holding the resource.

**Both `csv` and `excel` resources accept write-back.**

Note that Step's documentation says *"writing to a CSV file from a dataset is not
supported"*. That sentence is about the **`dataSet`** control, not `forEach` — a CSV
resource written through `forEach` + `script` persists normally.

For SQL, `writePKey` names the primary-key column Step uses to build the `UPDATE`.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Five plans, one per loop / data-source pattern |
| `data/records.csv` | Input rows, with an empty `Result` column for the write-back |
| `data/records.json` | The same data as JSON |
| `keywords/submitRecord.groovy` | Returns a `confirmationId` |
| `keywords/processFile.groovy` | Used by the documented drop-folder example |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

To run one plan only, use `--includePlans`. Note it is **comma-separated**, so a plan whose
name contains a comma cannot be selected — worth avoiding when you name plans.
