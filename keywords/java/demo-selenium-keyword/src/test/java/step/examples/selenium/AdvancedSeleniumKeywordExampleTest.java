package step.examples.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

import java.util.Map;

/**
 * This JUnit test is used to run the Step Keyword locally.
 * This class isn't used when the Keyword is executed in Step.
 */
public class AdvancedSeleniumKeywordExampleTest {

    private ExecutionContext ctx;

    @Before
    public void setUp() {
        // Define the required properties for the local execution
        Map<String, String> properties = Map.of("opencart.url", "https://opencart-prf.exense.ch/");
        ctx = KeywordRunner.getExecutionContext(properties, AdvancedSeleniumKeywordExample.class);
    }

    @Test
    public void test() throws Exception {
        // Call the Keyword with the required Keyword inputs
        ctx.run("Buy MacBook in OpenCart (advanced example)", "{\"Firstname\":\"Gustav\", \"Lastname\":\"Muster\"}");
    }

    @After
    public void tearDown() {
        ctx.close();
    }

}
