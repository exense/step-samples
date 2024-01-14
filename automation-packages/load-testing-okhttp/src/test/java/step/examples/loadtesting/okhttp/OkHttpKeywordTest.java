package step.examples.loadtesting.okhttp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.examples.loadtesting.okhttp.OkHttpKeywords;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import java.util.Map;

public class OkHttpKeywordTest {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        ctx = KeywordRunner.getExecutionContext(Map.of(), OkHttpKeywords.class);
    }

    @Test
    public void test() throws Exception {
        ctx.run("okhttp Init", "{}");
        ctx.run("OpenCart Home", "{}");
        ctx.run("OpenCart Add MacBook", "{}");
        ctx.run("OpenCart Checkout", "{}");
    }

    @After
    public void tearDown() {
        ctx.close();
    }

}
