package step.examples.loadtesting.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import java.util.Map;

// This uses the "traditional" way to test the Step keyword.
public class SeleniumKeywordsTest {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        ctx = KeywordRunner.getExecutionContext(Map.of(), SeleniumKeywords.class);
    }

    @Test
    public void test() throws Exception {
        ctx.run("Buy MacBook in OpenCart");
    }

    @After
    public void tearDown() {
        ctx.close();
    }

}
