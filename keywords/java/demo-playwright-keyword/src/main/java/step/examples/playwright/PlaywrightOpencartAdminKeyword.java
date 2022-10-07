package step.examples.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.PageImpl;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class PlaywrightOpencartAdminKeyword extends AbstractKeyword {

	@Keyword(name = "Playwright - Opencart Admin Login",
			schema = "{\"properties\":{\"headless\":{\"type\":\"boolean\"},"+
					"\"username\":{\"type\":\"string\"}, " +
					"\"password\":{\"type\":\"string\"} " +
					"}, \"required\":[]}")
	public void adminLoginInOpenCart()  {
		Playwright playwright = Playwright.create();
		session.put(playwright);
		Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(input.getBoolean("headless", true)));
		Page page = browser.newPage();
		session.put(page);
		page.navigate("https://opencart-prf.exense.ch/admin/");
		page.locator("#input-username").type(input.getString("username", "demo"));
		page.locator("#input-password").type(input.getString("password", "demo"));
		page.locator("button:has-text('Login')").click();
		page.locator("text= Catalog").click();
	}

	@Keyword(name = "Playwright - Opencart RPA Testcase - Update Product",
			schema = "{\"properties\":{\"product\":{\"type\":\"string\"},"+
					"\"quantity\":{\"type\":\"string\"} " +
					"}, \"required\":[]}")
	public void updateProduct() {
		Page page = session.get(PageImpl.class);

		page.locator("#menu-catalog >> a:has-text('Products')").click();
		String editXpath = "//td[text()='" + input.getString("product", "Apple Cinema 30\"") + "']/..//a[@data-original-title='Edit']";
		page.locator("xpath=" + editXpath).click();
		page.locator("[data-toggle=tab]:has-text('Data')").click();
		page.locator("#input-quantity").fill(input.getString("quantity", "1000"));
		page.locator("[data-original-title='Save']");
	}


}
