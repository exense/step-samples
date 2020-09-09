package step.examples.selenium;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
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

public class SeleniumKeywordExample extends AbstractKeyword {
	
	final List<String> defaultOptions = Arrays.asList(new String[] { "disable-infobars", "ignore-certificate-errors" });
	final List<String> headlessOptions = Arrays.asList(new String[] { "headless", "disable-gpu", "disable-sotfware-rasterizer" });
		
	@Keyword(name = "Open Chrome")
	public void Open_chrome_new() {										
		ChromeOptions options = new ChromeOptions();
		options.setAcceptInsecureCerts(true);
		options.addArguments(defaultOptions);
		options.setExperimentalOption("w3c", false);
        
		if (input.getBoolean("headless", false)) {
			options.addArguments(headlessOptions);
		}
		
		session.put("enableHarCapture", input.getBoolean("enableHarCapture", false));
				
					
		final WebDriver driver = new ChromeDriver(options);		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

		if (input.getBoolean("maximize", false)) {
			driver.manage().window().maximize();
		} else {
			driver.manage().window().setSize(new Dimension(1366,784));
		}

		setDriver(driver);
	}

	private static final String INPUT_SEARCH = "search";
	
	@Keyword(name = "Search in google", schema = "{\"properties\":{\""+INPUT_SEARCH+"\":{\"type\":\"string\"}}, \"required\":[\""+INPUT_SEARCH+"\"]}")
	public void searchInGoogle() throws Exception {
		validateInput();
		String searchString = input.getString(INPUT_SEARCH);

		WebDriver driver = getDriverFromSession();

		driver.get("http://www.google.com");

		WebElement searchInput = driver.findElement(By.name("q"));

		searchInput.sendKeys(searchString + Keys.ENTER);

		WebElement resultCountDiv = driver.findElement(By.xpath("//div/nobr"));

		List<WebElement> elements = driver.findElements(By.xpath("//div[@id='cnsw']/iframe"));
		if(elements.size()>0) {
			driver.switchTo().frame(elements.get(0));
			driver.findElement(By.xpath("//div[@id='introAgreeButton']")).click();
			new WebDriverWait(driver, 10).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='cnsw']/iframe")));
			driver.switchTo().defaultContent();
		}

		List<WebElement> resultHeaders = driver.findElements(By.xpath("//div[@class='r']//h3"));
		for (WebElement result : resultHeaders) {
			output.add(result.getText(), result.findElement(By.xpath("..//cite")).getText());
		}
		output.add("Results",String.valueOf(resultHeaders.size()));
		
		attachScreenshot(driver);
	}

	protected void validateInput() throws Exception {
		if (!input.containsKey(INPUT_SEARCH)) {
			throw new Exception("Input parameter 'search' not defined");
		}
	}
	
	private static final String INPUT_URL = "URL";
	
	@Keyword(name = "Navigate to", schema = "{\"properties\":{\""+INPUT_URL+"\":{\"type\":\"string\"}}, \"required\":[\""+INPUT_URL+"\"]}")
	public void navigateTo() throws Exception {
		WebDriver driver = getDriverFromSession();

		String url = input.getString(INPUT_URL);
		
		driver.navigate().to(url);
		
		attachScreenshot(driver);
	}

	private static final String INPUT_TEXT = "Text";
	
	@Keyword(name = "Click", schema = "{\"properties\":{\""+INPUT_TEXT+"\":{\"type\":\"string\"}}, \"required\":[\""+INPUT_TEXT+"\"]}")
	public void click() throws Exception {
		WebDriver driver = getDriverFromSession();

		String label = input.getString(INPUT_TEXT);
		
		WebElement element = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"+label.toLowerCase()+"']")));
		element.click();
		
		attachScreenshot(driver);
	}

	private static final String INPUT_XPATH = "XPath";

	@Keyword(name = "ClickByXPath", schema = "{\"properties\":{\""+INPUT_XPATH+"\":{\"type\":\"string\"}}, \"required\":[\""+INPUT_XPATH+"\"]}")
	public void clickByXPath() throws Exception {
		WebDriver driver = getDriverFromSession();

		String xpath = input.getString(INPUT_XPATH);

		WebElement element =new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
		element.click();

		attachScreenshot(driver);
	}
	
	protected WebDriver getDriverFromSession() throws Exception {
		if (session.get(DriverWrapper.class) == null) {
			throw new Exception(
					"Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
		}
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