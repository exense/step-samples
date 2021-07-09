# step-samples
Sample projects for *[step](https://step.exense.ch)*

## Overview

This repository contains various examples and templates of *step* artifacts (Keywords, Plans and *step* Client).

The repository is structured as follow:<br><br>
&nbsp;**/keywords**: contains various examples and templates of *step* Keywords for the different plugins (Java, .NET, Selenium, Cypress, etc)<br>
&nbsp;**/plans**: contains examples of *step* Plans<br>
&nbsp;**/step-client**: contains sample projects showcasing the use of the *step* Client relying on the *step* Controller API<br>

## Goal

The purpose of this repo, once you've familiarized yourself with the project's contents, is to help you package and deploy the keywords onto a *step* platform. For a local installation of *step*, just download the latest [release](https://github.com/exense/step/releases) and follow our [installation guidelines](https://step.exense.ch/knowledgebase/3.17/getting-started/quick-setup/).

Alternatively, if you're not interested in experimenting with a local instance and are not planing on running step on premise, you could skip this step and just contact us through our company [contact form](https://step.exense.ch/contact/) to request a cloud cluster directly. In that scenario, step is offered as a SaaS application and you don't have to worry about any operational aspects such as infrastructure, installation, upgrades or housekeeping.

## Setup

This project contains samples of open-source and enterprise features of *step*.

Samples of open-source features exclusively rely on public dependencies and should build without any specific prerequisite.

Enterprise samples require *step* Enterprise and rely on dependencies hosted on exense's private nexus. In order to use the enterprise samples, you'll have to add following lines to your maven settings.xml:

```xml
<servers>
	<server>
		<id>nexus-exense</id>
		<username>your_step_enterprise_username</username>
		<password>password</password>
	</server>
</servers>
<!-- if your compagny uses a proxy, add these lines: -->
<proxies>
	<proxy>
		<id>step-proxy</id>
		<active>true</active>
		<protocol>http</protocol>
		<host>your_proxy</host>
		<port>your_proxy_port</port>
		<nonProxyHosts>*.yourdomain</nonProxyHosts>
	</proxy>
</proxies>
```

## Help

For support, please check out our official [support page](https://step.exense.ch/support/) or [contact form](https://step.exense.ch/contact/).
