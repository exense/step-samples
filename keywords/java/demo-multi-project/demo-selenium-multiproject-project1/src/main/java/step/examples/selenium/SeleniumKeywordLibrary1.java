package step.examples.selenium;

import ch.exense.step.library.selenium.AbstractSeleniumKeyword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import step.handlers.javahandler.Keyword;

public class SeleniumKeywordLibrary1 extends AbstractSeleniumKeyword {

    @Keyword(name = "Open Chrome")
    public void openChrome() {
        WebDriver driver = new ChromeDriver();
        setDriver(driver);
    }

    @Keyword(name = "Navigate to exense")
    public void navigateToExense() throws Exception {
        WebDriver driver = getDriver();
        driver.get("http://www.exense.ch");
    }
}