package ch.exense.examples.oryon;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.Test;

import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class OryonKeywordsTest {

	ExecutionContext context;
	
	public void before() {
		// Put your Keyword properties here
		Map<String, String> properties = new HashMap<>();
		
		context = KeywordRunner.getExecutionContext(properties, OryonKeywords.class);
	}
	
	@Test
	public void test() throws Exception {
		context.run("Login","{\"Username\":\"MyUser\"}");
		Output<JsonObject> output = context.run("Search","{\"Criteria\":\"My search criteria\"}");
		assertEquals("My Output", output.getPayload().getString("Result"));
	}
}
