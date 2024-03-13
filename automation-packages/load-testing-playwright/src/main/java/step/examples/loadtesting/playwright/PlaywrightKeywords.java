package step.examples.loadtesting.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class PlaywrightKeywords extends AbstractKeyword {

    @Keyword(name = "Buy MacBook in OpenCart")
    public void buyMacBookInOpenCart() throws InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://opencart-prf.exense.ch/");
            page.locator("text=MacBook").click();
            // The previous click loads quite a few resources such as jQuery etc.;
            // If we don't include this wait, the next click may hang forever.
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.locator("text=Add to Cart").click();
            page.locator("text=1 item").click();
            page.locator("text=View Cart").click();
            page.locator("//a[text()='Checkout']").click();
            page.locator("text=Guest Checkout").click();
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            // Another timing issue potentially hanging the script
            Thread.sleep(500);
            page.locator("#button-account").click();
            page.locator("#input-payment-firstname").type("Gustav");
            page.locator("#input-payment-lastname").type("Muster");
            page.locator("#input-payment-email").type("customer@opencart.demo");
            page.locator("#input-payment-telephone").type("+41777777777");
            page.locator("#input-payment-address-1").type("Bahnhofstrasse 1");
            page.locator("#input-payment-city").type("Zurich");
            page.locator("#input-payment-postcode").type("8001");
            page.locator("#input-payment-country").selectOption(new SelectOption().setLabel("Switzerland"));
            page.locator("#input-payment-zone").selectOption(new SelectOption().setLabel("Zürich"));
            page.locator("#button-guest").click();
        }
    }
}