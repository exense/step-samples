package step.examples.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class SeleniumKeywordLibrary2Test {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        Map<String, String> properties = new HashMap<>();
        // Import the keywords of the library 1 and from this library
        ctx = KeywordRunner.getExecutionContext(properties, SeleniumKeywordLibrary1.class, SeleniumKeywordLibrary2.class);
    }

    @Test
    public void test() throws Exception {
        Output<JsonObject> result;
        // Call a Keyword from the common library
        ctx.run("Open Chrome");
        // Call a Keyword from the library 1
        ctx.run("Navigate to exense");
        // Call a Keyword from this library
        ctx.run("Navigate to exense consulting page");
    }

    @After
    public void tearDown() {
        ctx.close();
    }

}
