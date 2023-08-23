package step.examples.selenium;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import ch.exense.step.library.tests.LocalOnly;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.experimental.categories.Category;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

@Category(LocalOnly.class)
public class SeleniumKeywordExampleTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		Map<String, String> properties = new HashMap<>();
		ctx = KeywordRunner.getExecutionContext(properties, SeleniumKeywordExample.class);
	}

	@Test
	public void test() throws Exception {
		ctx.run("Buy MacBook in OpenCart", "{}");
	}

	@After
	public void tearDown() {
		ctx.close();
	}

}
