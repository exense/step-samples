---
practice: e2e-test-automation
framework: oryon
language: groovy
technology: syrius
strategy: keyword-driven
---

# Oryon Keyword Sample

Demonstrates how to write a Step keyword using the Oryon scripting runtime, which uses a Groovy-based DSL designed for the automation of Adcubum Syrius.

## What this sample shows

- Declaring an `Oryon` keyword in `automation-package.yaml` by pointing to a `.groovy` script file
- Writing automation logic using the Oryon Groovy DSL
- Structuring an Automation Package for a script-based keyword with no build step required

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: Oryon keyword declaration |
| `oryon-keywords/oryon-script-1.groovy` | Oryon script implementing the keyword logic |