package step.examples.playwright.opencart;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.handlers.javahandler.KeywordRunner;

import java.util.Map;

public class PlaywrightKeywordOpencartTest {
    private KeywordRunner.ExecutionContext ctx;

    @Before
    public void setUp() {
        ctx = KeywordRunner.getExecutionContext(Map.of(), PlaywrightKeywordOpencart.class);
    }

    @Test
    public void test() throws Exception {
        ctx.run("OpenCart - Buy MacBook in OpenCart");
    }

    @After
    public void tearDown() {
        ctx.close();
    }
}