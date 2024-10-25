package step.examples.loadtesting.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.time.Duration;

public class SeleniumKeywords extends AbstractKeyword {

    @Keyword(name = "Buy MacBook in OpenCart")
    public void buyMacBookInOpenCart() {
        ChromeOptions options = new ChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        try {
            // Measure the loading time of the homepage using the Keyword API
            output.startMeasure("Load homepage");
            driver.navigate().to("https://opencart-prf.exense.ch/");
            output.stopMeasure();

            output.startMeasure("Search article");
            driver.findElement(By.xpath("//*[text()='MacBook']")).click();
            WebElement addToCartButton = driver.findElement(By.xpath("//*[text()='Add to Cart']"));
            output.stopMeasure();

            addToCartButton.click();
            driver.findElement(By.xpath("//*[contains(text(),'1 item')]")).click();
            driver.findElement(By.xpath("//*[text()=' View Cart']")).click();
            driver.findElement(By.xpath("//*[text()='Checkout']")).click();
            driver.findElement(By.xpath("//input[@value='guest']")).click();
            Thread.sleep(500);
            driver.findElement(By.id("button-account")).click();
            driver.findElement(By.id("input-payment-firstname")).sendKeys("Gustav");
            driver.findElement(By.id("input-payment-lastname")).sendKeys("Muster");
            driver.findElement(By.id("input-payment-email")).sendKeys("customer@opencart.demo");
            driver.findElement(By.id("input-payment-telephone")).sendKeys("+41777777777");
            driver.findElement(By.id("input-payment-address-1")).sendKeys("Bahnhofstrasse 1");
            driver.findElement(By.id("input-payment-city")).sendKeys("Zurich");
            driver.findElement(By.id("input-payment-postcode")).sendKeys("8001");
            new Select(driver.findElement(By.id("input-payment-country"))).selectByVisibleText("Switzerland");
            new Select(driver.findElement(By.id("input-payment-zone"))).selectByVisibleText("Zürich");
            driver.findElement(By.id("button-guest")).click();
        } catch (Exception e) {
            // Rethrow the exception. It will be handled by Step properly
        } finally {
            driver.quit();
        }
    }
}