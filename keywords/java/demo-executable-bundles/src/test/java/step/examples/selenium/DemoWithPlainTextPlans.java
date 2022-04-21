package step.examples.selenium;

import org.junit.Test;
import org.junit.runner.RunWith;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;
import step.junit.runner.Step;
import step.junit.runners.annotations.ExecutionParameters;
import step.junit.runners.annotations.Plans;
import step.junit.runners.annotations.Plan;

@RunWith(Step.class)
// You can execute text plans file directly
@Plans({"GotoStep.plan","GotoProducts.plan"})
// Note: The ExecutionParameters annotation can only be defined on the class
@ExecutionParameters({"URL_EXENSE","http://exense.ch"})
public class DemoWithPlainTextPlans extends AbstractKeyword {

    @Plan("\"Open Chrome\"\n" +
          "\"Navigate to\" URL=\"${URL_EXENSE}\"\n" +
          "Click Text=\"Careers\"\n"+
          "Sleep Duration=1 Unit=\"s\"")
    // Note: The ExecutionParameters annotation can only be defined on the class
    @ExecutionParameters({"URL_EXENSE","http://exense.ch.not.found"})
    public void TestEmbeddedTextPlan() {
    }

    @Plan
    @Keyword
    public void TestSingleKeywordPlan() {
        output.add("key","value");
        //output.setBusinessError("Testing the errors");
    }
}
