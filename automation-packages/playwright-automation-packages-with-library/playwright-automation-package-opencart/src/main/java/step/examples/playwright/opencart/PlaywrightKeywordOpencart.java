package step.examples.playwright.opencart;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import step.core.reports.Measure;
import step.examples.playwright.library.AbstractPlaywrightKeyword;
import step.handlers.javahandler.Keyword;

/**
 * Playwright keywords for Opencart.
 * Demonstrates usage of the AbstractPlaywrightKeyword base class sharing the Playwright context in Session.
 */
public class PlaywrightKeywordOpencart extends AbstractPlaywrightKeyword {

    @Keyword(name = "OpenCart - Buy MacBook in OpenCart")
    public void buyMacBookInOpenCart() throws InterruptedException {
        Page page = getOrCreatePlaywrightPage();

        // Measure the loading time of the homepage using the Keyword API
        output.startMeasure("Load Opencart homepage");
        page.navigate("https://opencart-prf.stepcloud.ch/");
        output.stopMeasure();

        output.startMeasure("Search article");
        page.locator("text=MacBook").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();

        output.startMeasure("Add to cart and view cart");
        page.locator("text=Add to Cart").click();
        page.locator("text=1 item").click();
        page.locator("text=View Cart").click();
        output.stopMeasure();

        output.startMeasure("Checkout");
        page.locator("//a[text()='Checkout']").click();
        page.locator("text=Guest Checkout").click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
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
        output.stopMeasure();
    }
}
