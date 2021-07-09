package ch.exense.step.examples;

import static step.planbuilder.BaseArtefacts.for_;
import static step.planbuilder.BaseArtefacts.sequence;
import static step.planbuilder.FunctionArtefacts.keywordById;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.json.Json;
import javax.json.JsonObject;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import step.artefacts.Echo;
import step.artefacts.reports.CallFunctionReportNode;
import step.artefacts.reports.TestCaseReportNode;
import step.attachments.FileResolver;
import step.client.StepClient;
import step.client.executions.RemoteExecutionFuture;
import step.controller.multitenancy.Tenant;
import step.core.accessors.AbstractOrganizableObject;
import step.core.artefacts.reports.ReportNode;
import step.core.artefacts.reports.ReportNodeStatus;
import step.core.dynamicbeans.DynamicValue;
import step.core.plans.Plan;
import step.core.plans.builder.PlanBuilder;
import step.core.plans.runner.PlanRunnerResult;
import step.functions.Function;
import step.functions.execution.FunctionExecutionService;
import step.functions.io.FunctionInput;
import step.functions.io.Output;
import step.functions.packages.FunctionPackage;
import step.functions.type.FunctionTypeException;
import step.functions.type.SetupFunctionException;
import step.grid.TokenWrapper;
import step.grid.tokenpool.Interest;
import step.plans.nl.RootArtefactType;
import step.plans.nl.parser.PlanParser;
import step.plugins.java.GeneralScriptFunction;
import step.reporting.JUnit4ReportWriter;
import step.repositories.parser.StepsParser.ParsingException;

public class StepClientDemo {

	private String controllerUrl = "http://localhost:8080";
	private String user = "admin";
	private String password = "init";

	@Test
	public void controllerClientDemo()
			throws SetupFunctionException, FunctionTypeException, IOException, TimeoutException, InterruptedException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {

			// Create a DemoKeyword (javascript) and upload it to the controller
			Function keyword = uploadDemoKeyword(client);

			// Build a demo plan which calls the demo keyword
			Plan plan = PlanBuilder.create()
									.startBlock(sequence())
										.add(keywordById(keyword.getId().toString(), "{}"))
									.endBlock()
									.build();

			// Upload the plan to the controller
			client.getPlans().save(plan);

			// Execute the plan on the controller
			String executionId = client.getExecutionManager().execute(plan.getId().toString());

			RemoteExecutionFuture future = client.getExecutionManager().getFuture(executionId);

			// Wait for the plan execution to terminate and visit the report tree
			future.waitForExecutionToTerminate().visitReportTree(node -> {
				Assert.assertEquals(ReportNodeStatus.PASSED, node.getNode().getStatus());
			});
		}
	}

	@Test
	public void remotePlanRunnerDemo()
			throws SetupFunctionException, FunctionTypeException, IOException, TimeoutException, InterruptedException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			// Create a DemoKeyword (javascript) and upload it to the controller
			Function keyword = uploadDemoKeyword(client);

			// Build a demo plan which calls the demo keyword
			Plan plan = PlanBuilder.create()
					.startBlock(sequence())
						.add(keywordById(keyword.getId().toString(), "{}"))
					.endBlock().build();

			// Run the plan on the controller
			PlanRunnerResult result = client.getPlanRunners().getRemotePlanRunner().run(plan);

			// Wait for the plan execution to terminate and print the report tree to the
			// standard output
			result.waitForExecutionToTerminate().printTree();
		}
	}

	@Test
	public void listAndSelectTenants() throws Exception {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			List<Tenant> projects = client.getAvailableTenants();

			client.selectTenant(projects.get(0).getName());
		}
	}

	@Test
	public void planParserDemo() throws SetupFunctionException, FunctionTypeException, IOException, TimeoutException,
			InterruptedException, ParsingException {
		PlanParser planParser = new PlanParser();
		// Parse the plan in plan text format
		Plan plan = planParser.parse("For 1 to 10 \n" + "Echo 'HELLO' \n" + "End", RootArtefactType.TestCase);

		// Rename the plan
		plan.addAttribute(AbstractOrganizableObject.NAME, "My Testcase");

		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			// Run the plan on the controller
			PlanRunnerResult result = client.getPlanRunners().getRemotePlanRunner().run(plan);

			// Wait for the plan execution to terminate and generate a JUnit report
			result.waitForExecutionToTerminate().writeReport(new JUnit4ReportWriter(), new File("report.xml"));

			// The following would print the report tree to the standard output instead of
			// generating a JUnit report
			// result.waitForExecutionToTerminate().printTree();
		}
	}

	@Test
	public void runAnExistingPlan() throws IOException, TimeoutException, InterruptedException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			// Create a plan
			Plan plan = PlanBuilder.create()
					.startBlock(for_(1, 10))
						.add(new Echo())
					.endBlock().build();

			// upload it to the controller
			client.getPlans().save(plan);

			// The ID of the plan to be executed
			String planId = plan.getId().toString();

			// Set the execution parameters (the drop-downs that are set on the execution
			// screen in the UI)
			Map<String, String> executionParameters = new HashMap<>();
			executionParameters.put("env", "TEST");

			// Execute the plan
			String executionId = client.getExecutionManager().execute(planId, executionParameters);

			// Wait for the execution to terminate and visit the report tree
			client.getExecutionManager().getFuture(executionId).waitForExecutionToTerminate().visitReportNodes(node -> {
				if (node instanceof TestCaseReportNode) {
					// Do somethind....
				}
			});
		}
	}

	@Test
	public void functionManagerDemo() throws Exception {
		try (StepClient stepClient = new StepClient(controllerUrl, user, password)) {
			// Create a DemoKeyword (javascript) and upload it to the controller
			Function keyword = uploadDemoKeyword(stepClient);

			FunctionExecutionService functionExecutionService = stepClient.getFunctionExecutionService();

			// Select an agent token from the GRID
			Map<String, Interest> tokenSelectionCriteria = new HashMap<>();
			TokenWrapper tokenHandle = functionExecutionService.getTokenHandle(null, tokenSelectionCriteria, true,
					null);

			try {
				// Build the input object
				FunctionInput<JsonObject> input = new FunctionInput<JsonObject>();
				// Set the name of the Keyword
				input.setProperties(new HashMap<>());
				input.setPayload(Json.createObjectBuilder().build());

				// call the keyword executing it on the remote agent
				Output<JsonObject> result = functionExecutionService.callFunction(tokenHandle.getID(), keyword, input,
						JsonObject.class);
				// Assert that the Keyword has been executed properly
				Assert.assertEquals("OK :)", result.getPayload().getString("Result"));
			} finally {
				// Return the agent token to the GRID
				functionExecutionService.returnTokenHandle(tokenHandle.getID());
			}
		}
	}

	public void remoteControllerManagementDemo() throws IOException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			// Shutdown the controller gracefully
			client.getControllerServicesClient().shutdownController();
		}
	}

	protected GeneralScriptFunction uploadDemoKeyword(StepClient client)
			throws SetupFunctionException, FunctionTypeException {
		// Upload the javascript code of the keyword
		String resourceId = client.getResourceManager()
				.upload(new File(this.getClass().getResource("DemoKeyword.groovy").getFile())).getResourceId();

		GeneralScriptFunction f = createDemoKeyword(resourceId);

		// Save the keyword to the controller
		f = (GeneralScriptFunction) client.getFunctionManager().saveFunction(f);

		return f;
	}

	protected GeneralScriptFunction createDemoKeyword(String resourceId) {
		// Create the keyword configuration instance
		GeneralScriptFunction f = new GeneralScriptFunction();

		// Set the keyword attributes
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(Function.NAME, "MyDemoKeyword");

		f.setId(new ObjectId());
		f.setScriptLanguage(new DynamicValue<String>("groovy"));
		f.setScriptFile(new DynamicValue<String>(FileResolver.RESOURCE_PREFIX + resourceId));
		f.setAttributes(attributes);

		// Empty keyword schema
		f.setSchema(Json.createObjectBuilder().build());
		return f;
	}

	@Test
	public void localExecutionDemo() throws SetupFunctionException, FunctionTypeException, IOException,
			TimeoutException, InterruptedException, ParsingException {
		PlanParser planParser = new PlanParser();
		// Parse the plan in plan text format
		Plan plan = planParser.parse("For 1 to 3 \n" + "MyCustomKeyword someInput=\"hello\" \n"
				+ "Assert yourStringInputWas = \"hello\" \n" + "End", RootArtefactType.TestCase);

		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			// Run the plan locally by pointing to the class(es) containing the required
			// keyword(s)
			PlanRunnerResult result = client.getPlanRunners()
					.getLocalPlanRunner(new HashMap<>(), Arrays.asList(new Class[] { MyCustomKeyword.class }))
					.run(plan);

			// Wait for the plan execution to terminate and print the report tree in a
			// custom way (replacing CallFunction with actuall Keyword name)
			result.waitForExecutionToTerminate().visitReportTree(event -> {
				for (int i = 0; i < event.getStack().size(); i++) {
					System.out.print(" ");
				}
				ReportNode node = event.getNode();
				String nodeName = node.getName();
				if (node instanceof CallFunctionReportNode) {
					nodeName = ((CallFunctionReportNode) node).getFunctionAttributes().get("name");
				}
				System.out.print(nodeName + ":" + node.getStatus() + ":"
						+ (node.getError() != null ? node.getError().getMsg() : ""));
				System.out.print("\n");

			});
		}
	}

	@Test
	public void keywordPackageCreationAndDeletion() throws SetupFunctionException, FunctionTypeException, IOException,
			TimeoutException, InterruptedException, ParsingException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			Map<String, String> attributes = new HashMap<>();
			attributes.put("version", "1234");
			attributes.put("name", "myPackageName2");

			// Upload new function package
			FunctionPackage myKwPackage = client.getFunctionPackageClient().newKeywordPackage(null,
					new File("src/test/resources/ch/exense/step/examples/demo-java-keyword-0.0.1.jar"), attributes);

			client.getFunctionPackageClient().deleteKeywordPackage(myKwPackage.getId().toString());
		}
	}

	@Test
	public void idBasedKeywordPackageCreationAndUpdate() throws SetupFunctionException, FunctionTypeException,
			IOException, TimeoutException, InterruptedException, ParsingException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			Map<String, String> attributes = new HashMap<>();
			attributes.put("version", "1234");
			attributes.put("name", "myPackageName2");

			FunctionPackage myKwPackage = client.getFunctionPackageClient().newKeywordPackage(null,
					new File("src/test/resources/ch/exense/step/examples/demo-java-keyword-0.0.1.jar"), attributes);

			client.getFunctionPackageClient().updateKeywordPackageById(myKwPackage, null,
					new File("src/test/resources/ch/exense/step/examples/demo-java-keyword-0.0.1.jar"), attributes);

			// Only one copy of the KeywordPackage, Keywords and Resource have been created
			// and updated.
		}
	}

	@Test
	public void resourceBasedKeywordPackageCreationAndUpdate() throws SetupFunctionException, FunctionTypeException,
			IOException, TimeoutException, InterruptedException, ParsingException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			Map<String, String> attributes = new HashMap<>();
			attributes.put("version", "1234");
			attributes.put("name", "myPackageName2");

			for (int i = 0; i < 2; i++) {
				// Upload new function package
				client.getFunctionPackageClient().updateResourceBasedKeywordPackage(null,
						new File("src/test/resources/ch/exense/step/examples/demo-java-keyword-0.0.1.jar"), attributes);

			}

			// Only one keyword package is present on the server.
		}
	}

	@Test
	public void multipleKeywordPackagesWithResourceBasedCleanup() throws SetupFunctionException, FunctionTypeException,
			IOException, TimeoutException, InterruptedException, ParsingException {
		try (StepClient client = new StepClient(controllerUrl, user, password)) {
			Map<String, String> attributes = new HashMap<>();
			attributes.put("version", "1234");
			attributes.put("name", "myPackageName2");

			for (int i = 0; i < 2; i++) {
				// Upload new function package
				client.getFunctionPackageClient().newKeywordPackage(null,
						new File("src/test/resources/ch/exense/step/examples/demo-java-keyword-0.0.1.jar"), attributes);

			}

			// two keyword packages are created with the same resource name but different
			// id's (i.e copies).

			for (int i = 0; i < 2; i++) {
				FunctionPackage myKwPackage = client.getFunctionPackageClient()
						.lookupPackageByResourceName("demo-java-keyword-0.0.1.jar");

				// TODO fix this test which currently fails with 3.17
				client.getFunctionPackageClient().deleteKeywordPackage(myKwPackage.getId().toString());
			}

			// We cleaned up both copies via resourceName
		}
	}
}
