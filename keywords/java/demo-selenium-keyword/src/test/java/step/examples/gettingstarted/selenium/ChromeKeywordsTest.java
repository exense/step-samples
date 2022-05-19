package step.examples.gettingstarted.selenium;

import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import step.examples.selenium.SeleniumKeywordExample;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class ChromeKeywordsTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		ctx = KeywordRunner.getExecutionContext(ChromeKeywords.class);
	}

	@After
	public void closeSession() {
		ctx.close();
	}

	@Test
	public void createAndNavigateTest() throws Exception {
		ctx.run("createAndNavigate", "{}");
	}

	@Test
	public void createAndNavigate2Test() throws Exception {
		Output<JsonObject> output = ctx.run("createAndNavigate2", "{\"url\" : \"http://exense.ch/solutions\"}");
		System.out.println(output.getPayload());
		System.err.println(output.getError());
	}
	
}