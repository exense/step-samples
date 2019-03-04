# step-samples
Sample projects for STEP

## Prerequisites

* Maven is installed
* JDK 8 is installed
* Using a Java IDE is recommended (for instance Eclipse)
* Chrome is installed

## Overview

This repo contains two maven projects showcasing the basic capability of step's Keyword API.

The project *demo-java-keyword* contains a keyword class and package as well as a JUnit test class allowing you to understand how the java dependency and API work.

The project *demo-selenium-keyword* has similar content as the first project but this time, extended dependencies will allow the use of the chrome WebDriver and the corresponding JUnit test class will trigger a basic Selenium workflow.

## Goal

The purpose of this repo, once you've familiarized yourself with the project's contents, is to help you package and deploy the keywords onto a STEP platform. For a local installation, just download the latest [release](https://github.com/exense/step/releases) and follow our [installation guidelines](http://docs.exense.ch/wiki/step/view/Versions/3.9.x/Operations/#HInstallation).

ALternatively, if you're not interested in experimenting with a local instance and are not planing on running step on premise, you could skip this step and just contact us through our company contact form to request a cloud cluster directly. In that scenario, step is offered as a SaaS application and you don't have to worry about any operational aspects such as infrastructure, installation, upgrades or housekeeping.

## Project setup

Ideally using an IDE like Eclipse, and after cloning this repo and checking the prerequisites from above, you can start by just running the two JUnit classes. _JavaKeywordExampleTest_ will make use of keyword methods defined in _JavaKeywordExample_ and _SeleniumKeywordExampleTest_ will make use of keyword methods defined in _SeleniumKeywordExampleTest_. 

## Packaging

Just run maven's _package_ goal against the pom files of both projects. In addition to also executing the JUnit tests, two jar files containing the keyword classes will be built as a result: _demo-java-keyword-0.0.1-SNAPSHOT.jar_ _demo-selenium-keyword-0.0.1-SNAPSHOT-shaded.jar_ which you'll find in their corresponding project output folders.

## Deployment

Deployment onto either a local step instance or a cloud instance can then ensue. Please follow the recommendations from our [official documentation](http://docs.exense.ch/wiki/step/view/Versions/3.9.x/Development/#HPackaginganddeployment).

## Help

For support, please check out our official [support page](https://step.exense.ch/support/) or [contact form](https://step.exense.ch/contact/).
