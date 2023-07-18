package step.examples.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;

import javax.json.JsonObject;
import java.util.Map;

public class StatefulSeleniumKeywordExampleTest {

    private KeywordRunner.ExecutionContext ctx;

    @Before
    public void setUp() {
        ctx = KeywordRunner.getExecutionContext(Map.of(), StatefulSeleniumKeywordExample.class);
    }

    @Test
    public void test() throws Exception {
        Output<JsonObject> result;
        ctx.run("Open Chrome");
        ctx.run("Navigate to", "{ \"URL\" : \"http://www.exense.ch\" }");
        result = ctx.run("Click", "{ \"Text\" : \"Products\" }");
        Assert.assertNull(result.getError());
    }

    @After
    public void tearDown() {
        ctx.close();
    }
}
