---
use-case: e2e-testing
framework: oryon
language: groovy
target-platform: syrius
approach: keyword-driven
level: beginner
---

# Oryon Keyword Sample

Demonstrates how to write a Step keyword using the Oryon scripting runtime, which uses a Groovy-based DSL designed for rapid UI automation scripting.

## What this sample shows

- Declaring an `Oryon` keyword in `automation-package.yaml` by pointing to a `.groovy` script file
- Writing automation logic using the Oryon Groovy DSL
- Structuring an Automation Package for a script-based keyword with no build step required

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: Oryon keyword declaration |
| `oryon-keywords/oryon-script-1.groovy` | Oryon script implementing the keyword logic |
