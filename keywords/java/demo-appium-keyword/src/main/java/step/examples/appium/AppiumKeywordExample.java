package step.examples.appium;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.AndroidServerFlag;
import io.appium.java_client.touch.offset.PointOption;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class AppiumKeywordExample extends AbstractKeyword {
	private final String APPIUM_SERVICE = "appium_service";

	@Keyword
	public void startAppium() throws Exception {	
		String	bootstrapPortNumber = input.getString("bootstrapPortNumber", "1001");

		AppiumServiceBuilder builder = new AppiumServiceBuilder();
		builder.usingAnyFreePort().withArgument(AndroidServerFlag.BOOTSTRAP_PORT_NUMBER, bootstrapPortNumber);
		AppiumDriverLocalService service = AppiumDriverLocalService.buildService(builder);
		service.start();
		
		getSession().put(APPIUM_SERVICE, service);		
	}

	@Keyword
	public void stopAppium() throws Exception {
		AppiumDriverLocalService service = (AppiumDriverLocalService) getSession().get(APPIUM_SERVICE);
		service.stop();
	}
	
	@Keyword
	public void startEmulator() throws Exception {
		AppiumDriverLocalService service = (AppiumDriverLocalService) getSession().get(APPIUM_SERVICE);

		// Inputs
		String avd = input.getString("avd");
		String avdArgs = input.getString("avdArgs");
		int avdLaunchTimeout = input.getInt("avdLaunchTimeout");
		int avdReadyTimeout = input.getInt("avdReadyTimeout");
		int newCommandTimeout = input.getInt("newCommandTimeout");	
		String appPath = input.getString("appPath");
		boolean noReset = input.getBoolean("noReset", true);

		// Capabilities
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("avd", avd);
		capabilities.setCapability("avdArgs", avdArgs);
		capabilities.setCapability("avdLaunchTimeout", avdLaunchTimeout);
		capabilities.setCapability("avdReadyTimeout", avdReadyTimeout);
		capabilities.setCapability("newCommandTimeout", newCommandTimeout);
		capabilities.setCapability("deviceName", "Android Emulator");			
		capabilities.setCapability("autoLaunch", false);
		capabilities.setCapability("app", new File(appPath).getAbsolutePath());
		capabilities.setCapability("noReset", noReset);
		
		// Driver
		AppiumDriver<WebElement> driver = new AndroidDriver<WebElement>(service.getUrl(), capabilities);
		getSession().put("driverWrapper", new DriverWrapper(driver));
	}
	
	@Keyword
	public void startApplication() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();
		driver.launchApp();
	}
	
	@Keyword
	public void stopApplication() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();	
		driver.closeApp();		
	}
	
	@Keyword
	public void testClick() throws Exception {
		AppiumDriver<WebElement> driver = getDriver();
		
        WebElement el = driver.findElement(By.xpath(".//*[@text='Animation']"));
        assertEquals("Animation", el.getText());
        el = driver.findElementByClassName("android.widget.TextView");
        assertEquals("API Demos", el.getText());
        el = driver.findElement(By.xpath(".//*[@text='App']"));
        
        el.click();	        
        List<WebElement> els = driver.findElementsByClassName("android.widget.TextView");
        assertEquals("Activity", els.get(2).getText());

		driver.navigate().back();		        
	}
	
    @Keyword
    public void testSlider() throws Exception {
    	 AppiumDriver<WebElement> driver = getDriver();
	
        scrollTo("Views").click();      
        scrollTo("Seek Bar").click();
        WebElement slider = driver.findElementById("io.appium.android.apis:id/seek");
              
        Point sliderLocation = getCenter(slider);
        PointOption basePoint = new PointOption<>().withCoordinates(sliderLocation.getX(), sliderLocation.getY());
        PointOption targetPoint = new PointOption<>().withCoordinates(sliderLocation.getX() / 2, sliderLocation.getY());
        
        new TouchAction<>(driver)
        	.press(basePoint)
        	.moveTo(targetPoint)
        	.perform()
        	.release();
        slider = driver.findElementById("io.appium.android.apis:id/seek");
        driver.navigate().back();
        driver.navigate().back();
    }
    
    @Keyword
    public void displayTextClock() throws Exception {
    	AppiumDriver<WebElement> driver = getDriver();  
        scrollTo("Views").click();     
        scrollTo("TextClock").click();
        Thread.sleep(2000);
        driver.navigate().back();
        driver.navigate().back();
    }
    
    @Keyword
    public void animateDrawable() throws Exception {
    	AppiumDriver<WebElement> driver = getDriver();
        scrollTo("Graphics").click();     
        scrollTo("AnimateDrawables").click();
        Thread.sleep(2000);
        driver.navigate().back();
        driver.navigate().back();   	
    }
    
    private Point getCenter(WebElement element) {
        Point upperLeft = element.getLocation();
        Dimension dimensions = element.getSize();
        return new Point(upperLeft.getX() + dimensions.getWidth()/2, upperLeft.getY() + dimensions.getHeight()/2);
    }
    
    private MobileElement scrollTo(String text){
    	AppiumDriver<WebElement> driver = getDriver();     
        return (MobileElement) driver.findElement(MobileBy.
                AndroidUIAutomator("new UiScrollable(new UiSelector()"
                        + ".scrollable(true)).scrollIntoView(resourceId(\"android:id/list\")).scrollIntoView("
                        + "new UiSelector().text(\""+text+"\"))"));
    }
	
	// Wrapping the AppiumDriver instance as it is not implementing the Closeable interface
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
	
	private AppiumDriver<WebElement> getDriver() {
		return ((DriverWrapper) getSession().get("driverWrapper")).getDriver();
	}
}
