package step.examples.selenium.opencart.site;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import step.examples.selenium.opencart.page.IndexPage;
import step.examples.selenium.opencart.page.LoginPage;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.File;
import java.util.function.Supplier;

public class SiteKeywords extends AbstractKeyword {

	//Site is stored in the keyword session

	/*single keyword covering the full workflow*/
	@Keyword(name="Opencart_Selenium_Keyword")
	public void opencartSeleniumFullKeyword(){
		testLogin();
		testIndex();
		closeBrowser();
	}

	/*Individual keywords per browser interactions*/

	@Keyword(name="Login_page")
	public void loginPage() {
		WebDriver webDriver = SiteKeywords.webDriver();
		Site site = new Site(webDriver);
		/*Store the site and its web driver in session*/
		session.put(site);
		LoginPage page = site.gotoLoginPage();
	}

	@Keyword(name="Login")
	public void doLogin() {
		/*get the site from the session*/
		Site site = session.get(Site.class);
		/*Inputs passed dynamically to the keyword at execution (or use default value)*/
		String email = input.getString("email", "demo");
		String pwd = input.getString("pwd", "demo");
		/*perform login*/
		site.getLoginPage().setEmail(email).setPassword(pwd).clickSubmit();
	}

	@Keyword(name="Close_browser")
	public void closeBrowser() {
		session.get(Site.class).getWebDriver().close();
	}

	@Keyword(name="Home_page")
	public void testIndex() {
		IndexPage page = session.get(Site.class).gotoIndexPage();
		//After calling the home page we wait until the slide show is visible
		waitForElement(session.get(Site.class).getWebDriver(),
				"//*[@id=\"slideshow0\"]/div/div[3]",10);
	}

	@Keyword(name="Browse_category")
	public void browseCategory(){
		/*Retrieve site and related index page from session*/
		IndexPage indexPage = session.get(Site.class).getIndexPage();
		/*Get the category passed as input*/
		String category = input.getString("category");
		/*Create a custom measurement with the category name*/
		output.startMeasure("Browse_category_" + category);
		switch (category) {
			case "desktops" : indexPage.gotoDesktopsCategory();
				waitForContent();break;
			case "laptops" : indexPage.gotoLaptopsCategory();
				waitForContent();break;
			case "components": indexPage.gotoComponentsCategory();
				waitForContent();break;
			case "tablets" : indexPage.gotoTabletsCategory();
				waitForContent();break;
			default: output.setError("The provided category is not supported");
		}
		output.stopMeasure();
	}

	private void waitForContent() {
		waitForElement(session.get(Site.class).getWebDriver(),
				"//*[@id=\"content\"]/h2",10);
	}


	private void testLogin() {
		loginPage();
		doLogin();
	}

	public static WebDriver webDriver() {
		try {
			File file = new File("D:/Apps/WebDriver/chromedriver/chromedriver.exe");
			System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
			return new ChromeDriver();

		} catch (Exception e) {
			return null;
		}
	}

	protected void waitForElement(WebDriver driver, String xpath, long timeout) {
		By element = By.xpath(xpath);
		//calling helper method to poll until given web element is visible or a timeout occurs
		retryWhileFalse(() -> driver.findElement(element).isDisplayed(), timeout);
	}

	public static void retryWhileFalse(Supplier<Boolean> condition, long timeout) {
		long t1 = System.currentTimeMillis();
		Exception lastException = null;
		do {
			try {
				if (condition.get()) {
					return;
				}
			} catch (Exception e) {
				lastException = e;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} while (System.currentTimeMillis() < t1 + (timeout * 1000));
		throw new RuntimeException("Timeout while waiting for condition to apply.", lastException);
	}
}