package step.examples.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.util.concurrent.TimeUnit;

/**
 * This advanced example shows how to use Keyword properties and inputs in order to build reusable Keywords
 * It also shows how to use Keyword hooks in order to automatically generate screenshots
 * and attach them to the Keyword output on error.
 */
public class AdvancedSeleniumKeywordExample extends AbstractKeyword {

    private WebDriver driver;

    @Keyword(name = "Buy MacBook in OpenCart (advanced example)")
    public void buyMacBookInOpenCart() throws InterruptedException {
        // Read the URL from the keyword properties. These properties can be defined as Parameter in Step
        String url = properties.computeIfAbsent("opencart.url",
                k -> {
                    throw new RuntimeException("Missing property 'opencart.url'");
                });
        driver.navigate().to(url);
        driver.findElement(By.xpath("//*[text()='MacBook']")).click();
        driver.findElement(By.xpath("//*[text()='Add to Cart']")).click();
        driver.findElement(By.xpath("//*[contains(text(),'1 item')]")).click();
        driver.findElement(By.xpath("//*[text()=' View Cart']")).click();
        driver.findElement(By.xpath("//*[text()='Checkout']")).click();
        driver.findElement(By.xpath("//input[@value='guest']")).click();
        Thread.sleep(500);
        driver.findElement(By.id("button-account")).click();
        // Read the Firstname and Lastname from the Keyword Inputs
        driver.findElement(By.id("input-payment-firstname")).sendKeys(input.getString("Firstname"));
        driver.findElement(By.id("input-payment-lastname")).sendKeys(input.getString("Lastname"));
        driver.findElement(By.id("input-payment-email")).sendKeys("customer@opencart.demo");
        driver.findElement(By.id("input-payment-telephone")).sendKeys("+41777777777");
        driver.findElement(By.id("input-payment-address-1")).sendKeys("Bahnhofstrasse 1");
        driver.findElement(By.id("input-payment-city")).sendKeys("Zurich");
        driver.findElement(By.id("input-payment-postcode")).sendKeys("8001");
        new Select(driver.findElement(By.id("input-payment-country"))).selectByVisibleText("Switzerland");
        new Select(driver.findElement(By.id("input-payment-zone"))).selectByVisibleText("Zürich");
        driver.findElement(By.id("button-guest")).click();
    }

    @Override
    public void beforeKeyword(String keywordName, Keyword annotation) {
        ChromeOptions options = new ChromeOptions();
        // Set headless using the new recommended method
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    @Override
    public boolean onError(Exception e) {
        // Take a screenshot of the page and attach it to the output
        if (driver != null) {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, "screenshot.jpg");
            output.addAttachment(attachment);
        }
        return super.onError(e);
    }

    @Override
    public void afterKeyword(String keywordName, Keyword annotation) {
        // Ensure Playwright is properly closed after each keyword execution
        // to avoid process leaks on the agent
        driver.quit();
    }
}
