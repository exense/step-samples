package step.examples.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class SeleniumKeywordExample extends AbstractKeyword {

	@Keyword(name = "Open Chrome")
	public void openChrome() throws Exception {
		
		ChromeOptions options = new ChromeOptions();
		
		options.addArguments(Arrays.asList("disable-infobars"));
		
		boolean headless = input.getBoolean("headless", false);
		if (headless) {
			options.addArguments(Arrays.asList("headless","disable-gpu"));
		}
		boolean sandbox = input.getBoolean("sandbox", true);
		if (!sandbox) {
			options.addArguments(Arrays.asList("no-sandbox"));
		}
		
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		session.put(new DriverWrapper(driver));
	}

	@Keyword(name = "Search in google")
	public void searchInGoogle() throws Exception {
		if (input.containsKey("search")) {
			if (session.get(DriverWrapper.class) == null) {
				throw new Exception(
						"Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
			}
			WebDriver driver = session.get(DriverWrapper.class).driver;

			driver.get("http://www.google.com");

			WebElement searchInput = driver.findElement(By.name("q"));

			String searchString = input.getString("search");
			searchInput.sendKeys(searchString + Keys.ENTER);

			WebElement resultCountDiv = driver.findElement(By.xpath("//div/nobr"));

			List<WebElement> resultHeaders = driver.findElements(By.xpath("//div[@class='r']//h3"));
			for (WebElement result : resultHeaders) {
				output.add(result.getText(), result.findElement(By.xpath("..//cite")).getText());
			}
			
			attachScreenshot(driver);
			
		} else {
			throw new Exception("Input parameter 'search' not defined");
		}
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
	// Wrapping the WebDriver instance as it is not implementing the Closeable interface
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