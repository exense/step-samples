package step.examples.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import step.functions.io.OutputBuilder;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class SeleniumHelper {

	public static final int IMPLICIT_WAIT = 10;
	final static List<String> defaultOptions = Arrays.asList(new String[] { "disable-infobars", "ignore-certificate-errors", "reduce-security-for-testing", "password-store=basic" });
	final static List<String> headlessOptions = Arrays
			.asList(new String[] { "headless", "disable-gpu", "disable-sotfware-rasterizer" });

	public static WebDriver createDriver(boolean isHeadless) {
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		options.addArguments(defaultOptions);
		if (isHeadless) {
			options.addArguments(headlessOptions);
		}
		final Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("credentials_enable_service", false);
		chromePrefs.put("profile.password_manager_enabled", false);
		chromePrefs.put("profile.password_manager_leak_detection", false); // <======== This is the important one

		options.setExperimentalOption("prefs", chromePrefs);
		final WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(IMPLICIT_WAIT));
		//driver.manage().window().maximize();
		driver.manage().window().setSize(new Dimension(1920, 1080));
		return driver;
	}

	public static void attachScreenshot(WebDriver driver, OutputBuilder output) {
		try {
			byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, "screenshot.jpg");
			output.addAttachment(attachment);
		} catch (Exception ex) {
			output.appendError("Unable to generate screenshot");
		}
	}

	public static boolean onErrorHandler(Exception e, Optional<WebDriver> driver, OutputBuilder output) {
		driver.ifPresent(d -> attachScreenshot(d, output));

		//Report selenium exception as failed but not as technical error
		Throwable cause = Objects.requireNonNullElse(e.getCause(),e);
		if (WebDriverException.class.isAssignableFrom(cause.getClass())) {
			output.setBusinessError("Selenium error:" + cause.getMessage());
			return false;
		}
		return true;
	}

	// Wrapping the WebDriver instance to put it to the Session
	// as it is not implementing the Closeable interface
	public static class DriverWrapper implements Closeable {

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
