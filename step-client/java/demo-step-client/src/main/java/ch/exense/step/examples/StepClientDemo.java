package ch.exense.step.examples;

import org.junit.Test;
import step.client.StepClient;
import step.core.artefacts.reports.ReportNodeStatus;
import step.core.plans.Plan;
import step.core.plans.builder.PlanBuilder;
import step.core.plans.runner.PlanRunnerResult;
import step.planbuilder.BaseArtefacts;
import step.planbuilder.FunctionArtefacts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static step.planbuilder.BaseArtefacts.threadGroup;
import static step.planbuilder.FunctionArtefacts.keyword;

public class StepClientDemo {

    @Test
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        String controllerUrl = "http://localhost:8080";
        String user = "admin";
        String password = "init";
        // Using credentials for the demo. The use of an API key is recommended for productive use: StepClient(String controllerUrl, String token)
        try (StepClient client = new StepClient(controllerUrl, user, password)) {
            // The ID of the plan to be executed
            String planId = "...";

            // Set the execution parameters (the drop-downs that are set on the execution
            // screen in the UI)
            Map<String, String> executionParameters = new HashMap<>();
            executionParameters.put("env", "TEST");

            // Execute the plan
            String executionId = client.getExecutionManager().execute(planId, executionParameters);

            // Wait for the execution to terminate
            PlanRunnerResult result = client.getExecutionManager().getFuture(executionId).waitForExecutionToTerminate();

            if(result.getResult() == ReportNodeStatus.PASSED) {
                // Do something
                System.err.println("The Step execution failed");
                System.exit(1);
            }
        }
    }

    @Test
    public static void createAPlanViaPlanBuilderAndExecuteItInStep() throws IOException, TimeoutException, InterruptedException {
        String controllerUrl = "http://localhost:8080";
        String user = "admin";
        String password = "init";
        // Using credentials for the demo. The use of an API key is recommended for productive use: StepClient(String controllerUrl, String token)
        try (StepClient client = new StepClient(controllerUrl, user, password)) {
            Plan plan = PlanBuilder.create()
                    .startBlock(threadGroup(10, 5))
                    .add(keyword("MyKeyword"))
                    .endBlock().build();

            // Set the execution parameters (the drop-downs that are set on the execution
            // screen in the UI)
            Map<String, String> executionParameters = new HashMap<>();
            executionParameters.put("env", "TEST");

            plan = client.getPlans().save(plan);

            // Execute the plan
            String executionId = client.getExecutionManager().execute(plan.getId().toString(), executionParameters);

            // Wait for the execution to terminate
            PlanRunnerResult result = client.getExecutionManager().getFuture(executionId).waitForExecutionToTerminate();

            if(result.getResult() == ReportNodeStatus.PASSED) {
                // Do something
                System.err.println("The Step execution failed");
                System.exit(1);
            }
        }
    }
}
