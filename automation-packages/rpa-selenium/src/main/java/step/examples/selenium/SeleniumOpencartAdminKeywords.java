package step.examples.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.util.Optional;

import static step.examples.selenium.SeleniumHelper.*;


public class SeleniumOpencartAdminKeywords extends AbstractKeyword {

	@Keyword(name = "Opencart RPA Testcase - Admin login",
			schema = "{\"properties\":{\"headless\":{\"type\":\"boolean\"},"+
					"\"username\":{\"type\":\"string\"}, " +
					"\"password\":{\"type\":\"string\"} " +
					"}, \"required\":[]}")
	public void initOpencartRPAScenario() {
		WebDriver driver = createDriver(input.getBoolean("headless", true));
		this.session.put(new SeleniumHelper.DriverWrapper(driver));
		//Navigate to opencart website
		driver.get("https://opencart-prf.exense.ch/admin/");
		//Login
		driver.findElement(By.id("input-username")).sendKeys(input.getString("username", "demo"));
		driver.findElement(By.id("input-password")).sendKeys(input.getString("password", "demo"));
		driver.findElement(By.xpath("//button[text()=' Login']")).click();
		//Navigate to the Catalog
		driver.findElement(By.xpath("//a[text()=' Catalog']")).click();
	}

	@Keyword(name = "Opencart RPA Testcase - Update Product",
			schema = "{\"properties\":{\"product\":{\"type\":\"string\"},"+
					"\"quantity\":{\"type\":\"string\"} " +
					"}, \"required\":[]}")
	public void updateProduct() throws Exception {
		WebDriver driver = getNonNullDriverFromSession();

		//Navigate to products
		driver.findElement(By.xpath("//a[text()='Products']")).click();
		//Edit product
		String editXpath = "//td[text()='" + input.getString("product", "Apple Cinema 30\"") + "']/..//a[@data-original-title='Edit']";
		driver.findElement(By.xpath(editXpath)).click();
		//Switch to data tab
		driver.findElement(By.xpath("//a[text()='Data']")).click();
		//Update quantity and save
		WebElement element = driver.findElement(By.id("input-quantity"));
		element.clear();
		element.sendKeys(input.getString("quantity", "1000"));
		driver.findElement(By.xpath("//button[@data-original-title='Save']")).click();

	}

	/*
	 * Override the onError method to control how Selenium exceptions are reported in step
	 * It will also take a screenshot of the browser and add it to the report
	 */
	@Override
	public boolean onError(Exception e) {
		return onErrorHandler(e, getDriverFromSession(), output);
	}

	protected WebDriver getNonNullDriverFromSession() throws Exception {
		return getDriverFromSession().orElseThrow(()
				-> new Exception("Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword"));
	}

	protected Optional<WebDriver> getDriverFromSession() {
		DriverWrapper driverWrapper = session.get(DriverWrapper.class);
		return (driverWrapper!=null) ?
				Optional.of(driverWrapper.driver) :
				Optional.empty();
	}
}
