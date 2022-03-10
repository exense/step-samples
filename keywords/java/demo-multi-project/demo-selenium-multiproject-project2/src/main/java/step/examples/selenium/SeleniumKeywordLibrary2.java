package step.examples.selenium;

import ch.exense.step.library.selenium.AbstractSeleniumKeyword;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import step.handlers.javahandler.Keyword;

public class SeleniumKeywordLibrary2 extends AbstractSeleniumKeyword {

    @Keyword(name = "Navigate to exense consulting page")
    public void navigateToExenseConsultingPage() throws Exception {
        WebDriver driver = getDriver();
		driver.findElement(By.xpath("//*[text()='Consulting']")).click();
    }

}