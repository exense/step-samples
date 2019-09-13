package step.examples.appium.commons;

import java.io.Closeable;
import java.io.IOException;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
//Wrapping the AppiumDriver instance as it is not implementing the Closeable interface
public class DriverWrapper implements Closeable {
	private AppiumDriver<WebElement> driver;
	
	public DriverWrapper(AppiumDriver<WebElement> driver) {
		this.driver = driver;
	}

	@Override
	public void close() throws IOException {
		driver.quit();
	}
	
	public AppiumDriver<WebElement> getDriver() {
		return driver;
	}
}