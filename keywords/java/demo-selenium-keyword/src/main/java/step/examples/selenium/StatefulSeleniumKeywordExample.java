package step.examples.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.Closeable;
import java.time.Duration;

public class StatefulSeleniumKeywordExample extends AbstractKeyword {

    private static final int IMPLICIT_WAIT = 30;

    @Keyword(name = "Open Chrome")
    public void openChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(IMPLICIT_WAIT));
        addDriverToSession(driver);
    }

    private static final String INPUT_URL = "URL";

    @Keyword(name = "Navigate to", schema = "{\"properties\":{\"" + INPUT_URL
            + "\":{\"type\":\"string\"}}, \"required\":[\"" + INPUT_URL + "\"]}")
    public void navigateTo() throws Exception {
        WebDriver driver = getDriverFromSession();

        String url = input.getString(INPUT_URL);

        driver.navigate().to(url);

        attachScreenshotToOutput(driver);
    }

    private static final String INPUT_TEXT = "Text";

    @Keyword(name = "Click", schema = "{\"properties\":{\"" + INPUT_TEXT + "\":{\"type\":\"string\"}}, \"required\":[\""
            + INPUT_TEXT + "\"]}")
    public void click() throws Exception {
        WebDriver driver = getDriverFromSession();

        String label = input.getString(INPUT_TEXT);

        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                        + label.toLowerCase() + "']")));
        element.click();

        attachScreenshotToOutput(driver);
    }

    private static final String INPUT_XPATH = "XPath";

    @Keyword(name = "ClickByXPath", schema = "{\"properties\":{\"" + INPUT_XPATH
            + "\":{\"type\":\"string\"}}, \"required\":[\"" + INPUT_XPATH + "\"]}")
    public void clickByXPath() throws Exception {
        WebDriver driver = getDriverFromSession();

        String xpath = input.getString(INPUT_XPATH);

        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();

        attachScreenshotToOutput(driver);
    }

    protected WebDriver getDriverFromSession() throws Exception {
        DriverWrapper driverWrapper = session.get(DriverWrapper.class);
        if (driverWrapper == null) {
            throw new Exception(
                    "Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
        }
        return driverWrapper.driver;
    }

    public void attachScreenshotToOutput(WebDriver driver) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, "screenshot.jpg");
            output.addAttachment(attachment);
        } catch (Exception ex) {
            output.appendError("Unable to generate screenshot");
        }
    }

    private void addDriverToSession(WebDriver driver) {
        this.session.put(new DriverWrapper(driver));
    }

    // Wrapping the WebDriver instance to put it to the Session
    // as it is not implementing the Closeable interface
    public class DriverWrapper implements Closeable {

        final WebDriver driver;

        public DriverWrapper(WebDriver driver) {
            super();
            this.driver = driver;
        }

        @Override
        public void close() {
            // Calling driver.quit() when the session is closed
            driver.quit();
        }
    }
}
