package ch.exense.step.examples;

import org.junit.Test;
import step.client.StepClient;
import step.core.artefacts.reports.ReportNodeStatus;
import step.core.plans.runner.PlanRunnerResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
}
