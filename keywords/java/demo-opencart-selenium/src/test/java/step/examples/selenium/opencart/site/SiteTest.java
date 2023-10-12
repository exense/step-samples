package step.examples.selenium.opencart.site;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import step.examples.selenium.opencart.page.IndexPage;
import step.examples.selenium.opencart.page.LoginPage;

import java.io.File;

public class SiteTest {

	private Site site;

	@Before
	public void testLogin() {
		WebDriver webDriver = SiteTest.webDriver();
		site = new Site(webDriver);
		LoginPage page = site.gotoLoginPage();
		page.setEmail("demo").setPassword("demo").clickSubmit();
	}

	@After
	public void closeBrowser() {
		if (site.getWebDriver()!=null) {
			site.getWebDriver().close();
		}
	}

	@Test
	public void testIndex() {
		IndexPage page = site.gotoIndexPage();
		page.gotoDesktopsCategory().gotoLaptopsCategory().gotoComponentsCategory()
				.gotoTabletsCategory();
	}

	public static WebDriver webDriver() {
		return new ChromeDriver();
	}

}