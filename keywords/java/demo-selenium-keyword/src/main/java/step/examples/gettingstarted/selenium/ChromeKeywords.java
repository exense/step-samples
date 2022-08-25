package step.examples.gettingstarted.selenium;

import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class ChromeKeywords extends AbstractKeyword {

	@Keyword
	public void createAndNavigate() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments(Arrays.asList("no-sandbox","headless", "disable-gpu", "disable-sotfware-rasterizer"));
		
		// Uncomment the following and set the path to the chromedriver.exe if the chromedriver isn't available in your PATH 
		//System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
		ChromeDriver chrome = new ChromeDriver(options);
		String homeUrl = "http://www.exense.ch";
		chrome.navigate().to(homeUrl);
		chrome.findElement(By.xpath("//a[contains(text(),'Consulting')]")).click();

	    // Pausing artificially for demo
		pause();
		
		// Closing browser
		chrome.quit();
	}
	
	@Keyword
	public void createAndNavigate2() {
		// Uncomment the following and set the path to the chromedriver.exe if the chromedriver isn't available in your PATH 
		//System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
	    ChromeDriver chrome = new ChromeDriver();
	    
	    // Get the URL from the keyword input
	    String homeUrl = input.getString("url");
	    
	    chrome.navigate().to(homeUrl);
	    String extractedValue = chrome.findElement(By.xpath("//p[1]")).getText();

	    // Pausing artificially for demo
		pause();
		
		// Closing browser
	    chrome.quit();
	    
	    // Returning the extracted text to the keyword output
	    output.add("description", extractedValue);
	}
	
	
	public static void pause() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
