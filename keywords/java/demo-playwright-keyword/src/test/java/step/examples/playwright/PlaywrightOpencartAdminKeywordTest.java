package step.examples.playwright;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

public class PlaywrightOpencartAdminKeywordTest {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        Map<String, String> properties = new HashMap<>();
        ctx = KeywordRunner.getExecutionContext(properties, PlaywrightOpencartAdminKeyword.class);
    }

    @Test
    public void test() throws Exception {
        String input = Json.createObjectBuilder()
                .add("headless", false)
                .add("username", "demo")
                .add("password", "demo")
                .build().toString();

        Output<JsonObject> output;
        output = ctx.run("Playwright - Opencart Admin Login", input);
        assertNull(output.getError());

        input = Json.createObjectBuilder()
                .add("product", "Canon EOS 5D")
                .add("quantity","999")
                .build().toString();

        ctx.run("Playwright - Opencart RPA Testcase - Update Product", input);
        assertNull(output.getError());

        input = Json.createObjectBuilder()
                .add("product", "iPod Classic")
                .add("quantity","888")
                .build().toString();

        ctx.run("Playwright - Opencart RPA Testcase - Update Product",input);
        assertNull(output.getError());
    }

    @After
    public void tearDown() {
        ctx.close();
    }

}
