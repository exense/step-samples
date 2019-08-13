package step.examples.appium;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import step.examples.appium.commons.DriverWrapper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class AppiumKeywordExample extends AbstractKeyword {
	
	
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
    
	private AppiumDriver<WebElement> getDriver() {
		return ((DriverWrapper) getSession().get("driverWrapper")).getDriver();
	}
}
