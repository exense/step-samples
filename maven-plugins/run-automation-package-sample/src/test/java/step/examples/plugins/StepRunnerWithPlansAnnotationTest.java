/*******************************************************************************
 * Copyright (C) 2020, exense GmbH
 * 
 * This file is part of STEP
 *  
 * STEP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * STEP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with STEP.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package step.examples.plugins;

import org.junit.Assert;
import org.junit.runner.RunWith;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;
import step.junit.runner.Step;
import step.junit.runners.annotations.ExecutionParameters;
import step.junit.runners.annotations.Plan;
import step.junit.runners.annotations.Plans;

@RunWith(Step.class)
@Plans({"plan-sample.plan"})
@ExecutionParameters({"PARAM_EXEC","Value","PARAM_EXEC2","Value","PARAM_EXEC3","Value"})
public class StepRunnerWithPlansAnnotationTest extends AbstractKeyword {

	@Plan("planWithAssert\nAssert key = \"value\"")
	@Keyword
	public void planWithAssert() {
		output.add("key","value");
	}

	@Plan
	@Keyword
	public void explicitPlanWithExecutionParameter() {
		Assert.assertEquals("Value", properties.get("PARAM_EXEC"));
	}

	@Plan("Echo PARAM_EXEC")
	@Keyword
	public void inlinePlan() {}

}
