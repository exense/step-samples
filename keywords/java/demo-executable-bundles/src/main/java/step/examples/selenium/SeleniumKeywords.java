package step.examples.selenium;

import java.io.Closeable;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class SeleniumKeywords extends AbstractKeyword {

	@Keyword(name = "Open Chrome")
	public void openChrome() {
		ChromeOptions options = new ChromeOptions();
		WebDriver driver = new ChromeDriver(options);
		setDriver(driver);
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

	// Wrapping the WebDriver instance as it is not implementing the Closeable
	// interface
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