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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumKeywordExample extends AbstractKeyword {

    private static final int IMPLICIT_WAIT = 30;
    final List<String> defaultOptions = Arrays.asList("disable-infobars", "ignore-certificate-errors");
    final List<String> headlessOptions = Arrays
            .asList("headless", "disable-gpu", "disable-sotfware-rasterizer");

    @Keyword(name = "Open Chrome")
    public void openChrome() {
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments(defaultOptions);
        options.setExperimentalOption("w3c", false);

        if (input.getBoolean("headless", false)) {
            options.addArguments(headlessOptions);
        }

        session.put("enableHarCapture", input.getBoolean("enableHarCapture", false));

        final WebDriver driver = new ChromeDriver(options);
        setImplicitWait(driver, IMPLICIT_WAIT);
        driver.manage().timeouts().pageLoadTimeout(IMPLICIT_WAIT, TimeUnit.SECONDS);

        if (input.getBoolean("maximize", false)) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(new Dimension(1366, 784));
        }

        setDriver(driver);
    }

    private void setImplicitWait(final WebDriver driver, long implicitWaitIsSec) {
        driver.manage().timeouts().implicitlyWait(implicitWaitIsSec, TimeUnit.SECONDS);
    }

    private static final String INPUT_URL = "URL";

    @Keyword(name = "Navigate to", schema = "{\"properties\":{\"" + INPUT_URL
            + "\":{\"type\":\"string\"}}, \"required\":[\"" + INPUT_URL + "\"]}")
    public void navigateTo() throws Exception {
        WebDriver driver = getDriverFromSession();

        String url = input.getString(INPUT_URL);

        driver.navigate().to(url);

        attachScreenshot(driver);
    }

    private static final String INPUT_TEXT = "Text";

    @Keyword(name = "Click", schema = "{\"properties\":{\"" + INPUT_TEXT + "\":{\"type\":\"string\"}}, \"required\":[\""
            + INPUT_TEXT + "\"]}")
    public void click() throws Exception {
        WebDriver driver = getDriverFromSession();

        String label = input.getString(INPUT_TEXT);

        WebElement element = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                        + label.toLowerCase() + "']")));
        element.click();

        attachScreenshot(driver);
    }

    private static final String INPUT_XPATH = "XPath";

    @Keyword(name = "ClickByXPath", schema = "{\"properties\":{\"" + INPUT_XPATH
            + "\":{\"type\":\"string\"}}, \"required\":[\"" + INPUT_XPATH + "\"]}")
    public void clickByXPath() throws Exception {
        WebDriver driver = getDriverFromSession();

        String xpath = input.getString(INPUT_XPATH);

        WebElement element = new WebDriverWait(driver, 10)
                .until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();

        attachScreenshot(driver);
    }

    protected WebDriver getDriverFromSession() throws Exception {
        if (session.get(DriverWrapper.class) == null) {
            throw new Exception(
                    "Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
        }
        @SuppressWarnings("resource")
        WebDriver driver = session.get(DriverWrapper.class).driver;
        return driver;
    }

    public void attachScreenshot(WebDriver driver) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, "screenshot.jpg");
            output.addAttachment(attachment);
        } catch (Exception ex) {
            output.appendError("Unable to generate screenshot");
        }
    }

    private void setDriver(WebDriver driver) {
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
        public void close() throws IOException {
            driver.quit();
        }
    }
}