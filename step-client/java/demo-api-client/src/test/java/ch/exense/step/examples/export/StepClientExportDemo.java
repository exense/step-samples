package ch.exense.step.examples.export;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import step.client.StepClient;
import step.core.plans.Plan;
import step.core.plans.PlanAccessor;
import step.functions.Function;
import step.functions.manager.FunctionManager;
import step.functions.type.FunctionTypeException;
import step.functions.type.SetupFunctionException;
import step.plugins.functions.types.CompositeFunction;

public class StepClientExportDemo {
	
	@Test
	public void migrateCompositeKeywordsDemo() throws SetupFunctionException, FunctionTypeException, IOException, TimeoutException, InterruptedException {
		// Login to the origin and target controller
		try(StepClient originControllerClient = new StepClient("https://step-public-demo.stepcloud.ch", "admin", "public");
			StepClient targetControllerClient = new StepClient("http://...", "admin", "init");) {
			
			PlanAccessor originPlanAccessor = originControllerClient.getPlans();
			//PlanRepository originPlanRepository = originControllerClient.getPlans();
			FunctionManager originFunctionManager = originControllerClient.getFunctionManager();

			PlanAccessor targetPlanRepository = targetControllerClient.getPlans();
			FunctionManager targetFunctionManager = targetControllerClient.getFunctionManager();
			
			HashMap<String, String> attributes = new HashMap<>();
			attributes.put(Function.NAME, "TestComposite1");
			// get the composite function (keyword) definition by name
			Function function = originFunctionManager.getFunctionByAttributes(attributes);
			
			if(function instanceof CompositeFunction) {
				CompositeFunction compositeFunction = (CompositeFunction) function;
				
				// get the ID of the composite plan
				String planId = compositeFunction.getPlanId();
				Plan compositePlan = originPlanAccessor .get(planId);
				
				// save the composite function to the target controller
				targetFunctionManager.saveFunction(compositeFunction);
				// workaround: to be done twice to override default function initialization...
				targetFunctionManager.saveFunction(compositeFunction);
				// save the composite plan to the target controller
				targetPlanRepository.save(compositePlan);
			}
		}
	}
}
