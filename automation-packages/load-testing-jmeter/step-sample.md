---
practice: load-testing
framework: jmeter
language: xml
technology: web
strategy: project-integration
---

# Distributed Load Testing with JMeter

Wraps an existing JMeter test plan as a Step keyword and distributes its execution across multiple agents, enabling massively parallel JMeter load generation fully orchestrated by Step.

## What this sample shows

- Declaring a JMeter keyword directly in `automation-package.yaml` using the `JMeter` keyword type (no code required)
- Passing variables (e.g., target URL) from the Step plan into the JMeter test via keyword inputs
- Distributing JMeter execution across 10 agents with 50 iterations each (500 total)
- Auto-provisioning agents with `agents: auto_detect`

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: JMeter keyword and distributed Thread Group plan |
| `Demo_JMeter.jmx` | JMeter test plan targeting the OpenCart demo shop |