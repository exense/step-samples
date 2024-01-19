package step.examples.playwright;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class PlaywrightKeywordExampleTest {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        Map<String, String> properties = new HashMap<>();
        ctx = KeywordRunner.getExecutionContext(properties, PlaywrightKeywordExample.class);
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
