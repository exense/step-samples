package step.examples.selenium;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class SeleniumKeywordExampleTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		Map<String, String> properties = new HashMap<>();
		ctx = KeywordRunner.getExecutionContext(properties, SeleniumKeywordExample.class);
	}

	@After
	public void closeSession() {
		ctx.close();
	}

	@Test
	public void test() throws Exception {
		Output<JsonObject> result;
		result = ctx.run("Open Chrome","{\"headless\":true}");
		result = ctx.run("Search in google", "{ \"search\" : \"step exense\" }");
		result = ctx.run("Search in google", "{ \"search\" : \"exense djigger\" }");
		Assert.assertNull(result.getError());
		System.out.println(result.getPayload());
		Assert.assertTrue(result.getPayload().containsKey("exense/djigger: A production-ready monitoring and ... - GitHub"));
	}
	
	@Test
	public void test2() throws Exception {
		Output<JsonObject> result;
		result = ctx.run("Open Chrome","{\"headless\":true}");
		result = ctx.run("Navigate to", "{ \"URL\" : \"http://www.exense.ch\" }");
		result = ctx.run("Click", "{ \"Text\" : \"Products\" }");
		Assert.assertNull(result.getError());
	}

	@After
	public void tearDown() {
		ctx.close();
	}

}
