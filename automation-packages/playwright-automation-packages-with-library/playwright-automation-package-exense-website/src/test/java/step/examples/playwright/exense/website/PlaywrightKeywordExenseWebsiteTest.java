package step.examples.playwright.exense.website;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.handlers.javahandler.KeywordRunner;

import java.util.Map;

public class PlaywrightKeywordExenseWebsiteTest {

    private KeywordRunner.ExecutionContext ctx;

    @Before
    public void setUp() {
        ctx = KeywordRunner.getExecutionContext(Map.of(), PlaywrightKeywordExenseWebsite.class);
    }

    @Test
    public void test() throws Exception {
        ctx.run("Exense Website - Simple navigation");
    }

    @After
    public void tearDown() {
        ctx.close();
    }
}