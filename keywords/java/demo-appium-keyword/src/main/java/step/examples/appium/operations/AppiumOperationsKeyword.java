package step.examples.appium.operations;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.appmanagement.AndroidInstallApplicationOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import step.examples.appium.commons.DriverWrapper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class AppiumOperationsKeyword extends AbstractKeyword {
	private final String APPIUM_SERVICE = "appium_service";

	@Keyword
	public void startAppium() throws Exception {	
		String bootstrapPortNumber = input.getString("bootstrapPortNumber", "1001");

		AppiumServiceBuilder builder = new AppiumServiceBuilder();
		builder.usingAnyFreePort().withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER, bootstrapPortNumber);
		if(input.containsKey("chromeDriverPath")) builder.usingAnyFreePort().withArgument(AndroidServerFlag.CHROME_DRIVER_EXECUTABLE, input.getString("chromeDriverPath"));	
		AppiumDriverLocalService service = AppiumDriverLocalService.buildService(builder);
		service.start();
		
		getSession().put(APPIUM_SERVICE, service);		
	}

	@Keyword
	public void stopAppium() throws Exception {
		AppiumDriverLocalService service = getAppiumDriverLocalService();
		service.stop();
	}
	
	@Keyword
	public void startEmulator() throws Exception {
		AppiumDriverLocalService service = getAppiumDriverLocalService();

		String avd = input.getString("avd");
		String avdArgs = input.getString("avdArgs");
		int avdLaunchTimeout = input.getInt("avdLaunchTimeout");
		int avdReadyTimeout = input.getInt("avdReadyTimeout");
		int newCommandTimeout = input.getInt("newCommandTimeout");	
		String appPath = input.getString("appPath");
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("avd", avd);
		capabilities.setCapability("avdArgs", avdArgs);
		capabilities.setCapability("avdLaunchTimeout", avdLaunchTimeout);
		capabilities.setCapability("avdReadyTimeout", avdReadyTimeout);
		capabilities.setCapability("newCommandTimeout", newCommandTimeout);
		capabilities.setCapability("app", appPath);

		capabilities.setCapability("deviceName", input.getString("deviceName", "Android Emulator"));
		capabilities.setCapability("autoLaunch", input.getBoolean("autoLaunch", false));
		capabilities.setCapability("noReset", input.getBoolean("noReset", true));
		
		if(input.containsKey("appPackage")) capabilities.setCapability("appPackage", input.getString("appPackage"));
		if(input.containsKey("appActivity")) capabilities.setCapability("appActivity", input.getString("appActivity"));
		if(input.containsKey("optionalIntentArguments")) capabilities.setCapability("optionalIntentArguments", input.getString("optionalIntentArguments"));		
		
		AppiumDriver<WebElement> driver = new AndroidDriver<WebElement>(service.getUrl(), capabilities);
		getSession().put("driverWrapper", new DriverWrapper(driver));
	}
	
	@Keyword
	public void startChrome() {
		AppiumDriverLocalService service = getAppiumDriverLocalService();

		String avd = input.getString("avd");
		String avdArgs = input.getString("avdArgs");
		int avdLaunchTimeout = input.getInt("avdLaunchTimeout");
		int avdReadyTimeout = input.getInt("avdReadyTimeout");
		int newCommandTimeout = input.getInt("newCommandTimeout");
		
		String platformName = input.getString("platformName", "Android");
		String platformVersion = input.getString("platformVersion", "8.0");
		String browserName = input.getString("browserName", "Chrome");

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("avd", avd);
		capabilities.setCapability("avdArgs", avdArgs);
		capabilities.setCapability("avdLaunchTimeout", avdLaunchTimeout);
		capabilities.setCapability("avdReadyTimeout", avdReadyTimeout);
		capabilities.setCapability("newCommandTimeout", newCommandTimeout);
		
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("browserName", browserName);
		
		capabilities.setCapability("deviceName", input.getString("deviceName", "Android Emulator"));
		capabilities.setCapability("noReset", input.getBoolean("noReset", true));	
	
		AppiumDriver<WebElement> driver = new AndroidDriver<WebElement>(service.getUrl(), capabilities);				
		getSession().put("driverWrapper", new DriverWrapper(driver));
	}
	
	@Keyword
	public void installApplication() throws Exception {
		String appPath = input.getString("appPath");
		AppiumDriver<WebElement> driver = getDriver();
		AndroidInstallApplicationOptions options = new AndroidInstallApplicationOptions();
		options.withTimeout(Duration.ofMillis(Integer.valueOf(input.getString("timeout", "60000"))));
		driver.installApp(appPath, options);			
	}
	
	@Keyword
	public void startApplication() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();
		driver.launchApp();
	}

	@Keyword
	public void resetApplication() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();
		driver.resetApp();	
	}
	
	@Keyword
	public void stopApplication() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();	
		driver.closeApp();		
	}
	
	private AppiumDriverLocalService getAppiumDriverLocalService() {
		return (AppiumDriverLocalService) getSession().get(APPIUM_SERVICE);
	}
	
	private AppiumDriver<WebElement> getDriver() {
		return ((DriverWrapper) getSession().get("driverWrapper")).getDriver();
	}
}
