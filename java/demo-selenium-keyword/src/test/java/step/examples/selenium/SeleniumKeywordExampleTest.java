package step.examples.selenium;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import step.examples.selenium.SeleniumKeywordExample;
import step.grid.io.OutputMessage;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class SeleniumKeywordExampleTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		Map<String, String> properties = new HashMap<>();
		ctx = KeywordRunner.getExecutionContext(properties, SeleniumKeywordExample.class);
	}

	@Test
	public void test() throws Exception {
		OutputMessage result;
		result = ctx.run("Open Chrome");
		result = ctx.run("Search in google", "{ \"search\" : \"exense step\" }");
		result = ctx.run("Search in google", "{ \"search\" : \"exense djigger\" }");
		Assert.assertNull(result.getError());
		Assert.assertTrue(result.getPayload().containsKey("GitHub - exense/djigger: A production-ready monitoring and profiling ..."));
	}

	@After
	public void tearDown() {
		ctx.close();
	}

}
