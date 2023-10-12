package step.examples.selenium.opencart.site;

import org.junit.Assert;
import org.junit.Test;
import step.functions.io.Output;
import step.handlers.javahandler.KeywordRunner;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class OpencartSeleniumKeywordTest {

    @Test
    public void opencartSeleniumKeywordTest() throws Exception {
        Map<String, String> properties = new HashMap<>();
        KeywordRunner.ExecutionContext ctx = KeywordRunner.getExecutionContext(properties, SiteKeywords.class);
        ctx.run("Opencart_Selenium_Keyword", "{}");
    }

    @Test
    public void opencartSeleniumKeywordLeveragedloginTest() throws Exception {
        Map<String, String> properties = new HashMap<>();
        /*Execute the keyword providing email and password as input*/
        KeywordRunner.ExecutionContext ctx = KeywordRunner.getExecutionContext(properties, SiteKeywords.class);
        Output<JsonObject> keyword_output = ctx.run("Opencart_Selenium_Keyword", "{\"email\":\"demo\",\"pwd\":\"demo\"}");
        /*validate that the keyword execution had no errors*/
        Assert.assertNull(keyword_output.getError());
    }

    @Test
    public void opencartSeleniumSingleKeywordsTest() throws Exception {
        Map<String, String> properties = new HashMap<>();
        KeywordRunner.ExecutionContext ctx = KeywordRunner.getExecutionContext(properties, SiteKeywords.class);
        /*Go to the login page*/
        Output<JsonObject> keyword_output = ctx.run("Login_page", "{}");
        Assert.assertNull(keyword_output.getError());
        /*Perform the login*/
        keyword_output = ctx.run("Login", "{\"email\":\"demo\",\"pwd\":\"demo\"}");
        Assert.assertNull(keyword_output.getError());
        /*Go to the home page*/
        keyword_output = ctx.run("Home_page", "{}");
        Assert.assertNull(keyword_output.getError());
        /*Browse desktop category*/
        keyword_output = ctx.run("Browse_category", "{\"category\":\"desktops\"}");
        Assert.assertNull(keyword_output.getError());
        /*Browse laptops category*/
        keyword_output = ctx.run("Browse_category", "{\"category\":\"laptops\"}");
        Assert.assertNull(keyword_output.getError());
        /*Close the browser*/
        keyword_output = ctx.run("Close_browser", "{}");
        Assert.assertNull(keyword_output.getError());
    }
}
