package step.examples.appium;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import step.examples.appium.commons.DriverWrapper;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class AppiumAtomicKeywordsExample extends AbstractKeyword {

	
	@Keyword(schema="{\"properties\":{\"text\":{\"type\":\"string\"}}, \"required\":[\"text\"]}")
	public void Click() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();

		WebElement el = driver.findElement(By.xpath(".//*[@text='" + input.getString("text") + "']"));
		el.click();
		
		attachScreenshotIfDebugEnabled();
	}

	@Keyword(schema="{\"properties\":{\"text\":{\"type\":\"string\"}}, \"required\":[\"text\"]}")
	public void Scroll_to() throws Exception {
		scrollTo(input.getString("text"));
		
		attachScreenshotIfDebugEnabled();
	}

	@Keyword
	public void Back() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();
		driver.navigate().back();
		
		attachScreenshotIfDebugEnabled();
	}

	
	public boolean onError(Exception e) {
		attachScreenshot();
		return super.onError(e);
	}

	private MobileElement scrollTo(String text) {
		AppiumDriver<WebElement> driver = getDriver();
		return (MobileElement) driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView(resourceId(\"android:id/list\")).scrollIntoView("
						+ "new UiSelector().text(\"" + text + "\"))"));
	}

	private AppiumDriver<WebElement> getDriver() {
		return ((DriverWrapper) getSession().get("driverWrapper")).getDriver();
	}
	
	private void attachScreenshotIfDebugEnabled() {
		if(properties.getOrDefault("debug", "false").equals("true")) {
			attachScreenshot();
		}
	}

	private void attachScreenshot() {
		AppiumDriver<WebElement> driver = getDriver();
		try {
			byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, "screenshot.jpg");
			output.addAttachment(attachment);
		} catch (Exception ex) {
			output.appendError("Unable to generate screenshot");
		}
	}
}
