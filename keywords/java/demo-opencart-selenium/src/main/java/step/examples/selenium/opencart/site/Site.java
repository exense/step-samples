package step.examples.selenium.opencart.site;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import step.examples.selenium.opencart.object.PageObject;
import step.examples.selenium.opencart.page.IndexPage;
import step.examples.selenium.opencart.page.LoginPage;

import java.io.Closeable;
import java.io.IOException;

@PageObject
public class Site implements Closeable {

	private static final String BASE_URL = "http://demo.opencart.com/";

	private final WebDriver driver;

	private final LoginPage loginPage;

	private final IndexPage indexPage;

	public Site(WebDriver webDriver) {
		this.driver = webDriver;
		this.loginPage = new LoginPage(driver);
		this.indexPage = new IndexPage(driver);
	}

	public LoginPage gotoLoginPage() {
		driver.get(BASE_URL + "index.php?route=account/login");
		PageFactory.initElements(driver, loginPage);
		return loginPage;
	}

	public IndexPage gotoIndexPage() {
		driver.get(BASE_URL + "index.php");
		PageFactory.initElements(driver, indexPage);
		return indexPage;
	}

	public LoginPage getLoginPage() {
		return loginPage;
	}

	public IndexPage getIndexPage() {
		return indexPage;
	}

	public WebDriver getWebDriver() {
		return driver;
	}

	@Override
	public void close() throws IOException {
		if (driver!=null) {
			driver.close();
		}
	}
}
