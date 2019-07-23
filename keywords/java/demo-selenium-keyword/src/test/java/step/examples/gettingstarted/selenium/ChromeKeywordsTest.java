package step.examples.gettingstarted.selenium;

import javax.json.JsonObject;

import org.junit.Test;

import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class ChromeKeywordsTest {

	@Test
	public void createAndNavigateTest() throws Exception {
		ExecutionContext ctx = KeywordRunner.getExecutionContext(ChromeKeywords.class);
		ctx.run("createAndNavigate", "{}");
	}

	@Test
	public void createAndNavigate2Test() throws Exception {
		ExecutionContext ctx = KeywordRunner.getExecutionContext(ChromeKeywords.class);
		Output<JsonObject> output = ctx.run("createAndNavigate2", "{\"url\" : \"http://exense.ch/solutions\"}");
		System.out.println(output.getPayload());
		System.err.println(output.getError());
	}
	
}