---
practice: load-testing
framework: appium
language: java
technology: mobile
strategy: keyword-driven
---

# Load Testing with Appium (Android)

Demonstrates mobile automation and load testing on an Android application using Appium and Step. The Sauce Labs demo app (`mda-2.2.0-25.apk`) is used as the system under test, running on an Android emulator.

## What this sample shows

- Writing a Step keyword in Java that drives an Android app via the Appium Java client
- Distributing mobile test execution across multiple Step agents using a Thread Group
- Setting up the required Android SDK, AVD emulator, and Appium server on each agent
- Running the keyword locally with JUnit before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build — produces the shaded jar for deployment |
| `Readme.md` | Full setup guide: Android SDK, AVD, Appium, and Appium Inspector |
| `src/main/resources/automation-package.yaml` | Package definition: Thread Group plan and keyword declaration |
| `src/main/resources/mda-2.2.0-25.apk` | Sauce Labs demo Android app (must be downloaded separately) |