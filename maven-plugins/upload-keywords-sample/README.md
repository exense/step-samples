The example demonstrates uploading a keyword package to Step during a project build via maven.
On the `package` phase the `PlaywrightKeywordExample` keyword (implementing the example from https://step.exense.ch/resources/load-testing-with-playwright)
is uploaded as `jar-with-dependency` artifact to Step server.

Before you launch this example you have to specify the following maven properties in `pom.xml` (or pass them as -D command line arguments):
* step.server-url - the url of Step server, where you want to execute the test plan
* step.auth-token (for EE only) - the auth token of Step user (TODO: link to the guide)
* step.project-name (for EE only) - the project name in Step

For Step OS:
`mvn clean package -P StepOS "-Dstep.server-url=http://localhost:4201" `

For Step EE:
`mvn clean package -P StepEE "-Dstep.server-url=http://localhost:4201" "-Dstep.project-name=Common" "-Dstep.auth-token=eyJhbGciOiJIUzI1NiJ9.eyJqdGki...."`

To learn more about maven plugins for Step please follow this link:
TODO: add link on maven plugin documentation