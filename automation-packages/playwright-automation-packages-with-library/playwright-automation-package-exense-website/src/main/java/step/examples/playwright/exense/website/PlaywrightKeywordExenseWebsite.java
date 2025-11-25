package step.examples.playwright.exense.website;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import step.examples.playwright.library.AbstractPlaywrightKeyword;
import step.handlers.javahandler.Keyword;

/**
 * Playwright keywords for App2.
 * Demonstrates usage of the AbstractPlaywrightKeyword base class.
 */
public class PlaywrightKeywordExenseWebsite extends AbstractPlaywrightKeyword {

    @Keyword(name = "Exense Website - Simple navigation")
    public void buyMacBookInOpenCart() throws InterruptedException {
        Page page = getOrCreatePlaywrightPage();

        // Measure the loading time of the homepage using the Keyword API
        output.startMeasure("Load Exense Website homepage");
        page.navigate("https://www.exense.ch/");
        output.stopMeasure();

        output.startMeasure("Consulting");
        page.locator("text=Consulting").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();

        output.startMeasure("Products");
        page.locator("text=Products").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();

        output.startMeasure("About us");
        page.locator("text=About us").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();

        output.startMeasure("Careers");
        page.locator("text=Careers").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();

        output.startMeasure("contact us");
        page.locator("text=contact us").first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        output.stopMeasure();
    }
}
