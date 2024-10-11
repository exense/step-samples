package step.examples.serenity.test;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * The JUnit runner that executes all the features
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
		plugin = {"html:reports/cucumber-html-report.html",
				"json:reports/cucumber.json"}
		,features= {"classpath:features"}
		,glue = {"step.examples.serenity.stepdefinition"}
		,monochrome = true
		)
public class SerenityTestRunner {

}
