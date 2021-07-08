package step.examples.sikulix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class SikuliKeywordsTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		Map<String, String> properties = new HashMap<>();
		ctx = KeywordRunner.getExecutionContext(properties, SikuliKeywords.class);
	}

	@Test
	public void test() throws Exception {
		Output<JsonObject> result = ctx.run("openWord");
		assertNull(result.getError());
	}

	@After
	public void tearDown() {
		ctx.close();
	}

}
