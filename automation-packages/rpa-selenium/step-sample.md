---
use-case: rpa
framework: selenium
language: java
target-platform: web
approach: keyword-driven
level: intermediate
---

# RPA with Selenium (Java)

Automates a back-office data entry task: logs into the OpenCart admin panel and bulk-updates product inventory quantities from a CSV file. Demonstrates how Step's `forEach` / CSV data source drives repetitive UI automation at scale.

## What this sample shows

- Automating an admin/back-office UI workflow with Selenium WebDriver keywords
- Iterating over CSV data rows with the `forEach` / `csv` plan node to process multiple records
- Passing CSV values as keyword inputs using row expressions (`row.Product`, `row.Quantity`)
- Running the keyword with a visible browser (`headless: false`) for supervised RPA execution

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `src/main/resources/automation-package.yaml` | Plan: admin login keyword + `forEach` CSV loop for product updates |
| `src/main/resources/test-data/Opencart_inventory.csv` | Input data: product names and target quantities |
| `src/main/java/.../SeleniumRpaKeywords.java` | Keywords: admin login and product quantity update |
