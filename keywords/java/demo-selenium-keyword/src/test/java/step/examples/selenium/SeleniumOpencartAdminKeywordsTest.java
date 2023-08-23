package step.examples.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import step.examples.LocalOnly;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Map;

import static org.junit.Assert.assertNull;

@Category(LocalOnly.class)
public class SeleniumOpencartAdminKeywordsTest {

    private KeywordRunner.ExecutionContext ctx;

    @Before
    public void before() {
        Map<String, String> properties = Map.of();
        ctx = KeywordRunner.getExecutionContext(properties, SeleniumOpencartAdminKeywords.class);
    }

    @After
    public void tearDown() {
        if (ctx != null) {
            ctx.close();
        }
    }

    @Test
    public void testSeleniumOpencartAdminKeywordsTest() throws Exception {
        String input = Json.createObjectBuilder()
                .add("username", "demo")
                .add("password", "demo")
                .build().toString();

        Output<JsonObject> output;
        output = ctx.run("Opencart RPA Testcase - Admin login", input);
        assertNull(output.getError());

        input = Json.createObjectBuilder()
                .add("product", "Canon EOS 5D")
                .add("quantity", "999")
                .build().toString();

        ctx.run("Opencart RPA Testcase - Update Product", input);
        assertNull(output.getError());

        input = Json.createObjectBuilder()
                .add("product", "iPod Classic")
                .add("quantity", "888")
                .build().toString();

        ctx.run("Opencart RPA Testcase - Update Product", input);
        assertNull(output.getError());

    }

}