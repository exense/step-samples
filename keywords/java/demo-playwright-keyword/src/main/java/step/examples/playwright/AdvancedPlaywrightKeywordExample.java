package step.examples.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This advanced example shows how to use Step Keyword hooks in order to automatically generate screenshots
 * and playwright traces and attach them to the Keyword output on error.
 * It also shows how to use properties and Keyword inputs in order to build reusable Keywords.
 */
public class AdvancedPlaywrightKeywordExample extends AbstractKeyword {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private BrowserContext context;

    @Keyword(name = "Buy MacBook in OpenCart - Advanced")
    public void buyMacBookInOpenCart() throws InterruptedException {
        // Read the URL from the keyword properties. These properties can be defined as Parameter in Step
        page.navigate(properties.computeIfAbsent("opencart.url",
                k -> {throw new RuntimeException("Missing property 'opencart.url'");}));
        page.locator("text=MacBook").click();
        page.locator("text=Add to Cart").click();
        page.locator("text=1 item").click();
        page.locator("text=View Cart").click();
        page.locator("//a[text()='Checkout']").click();
        page.locator("text=Guest Checkout").click();
        Thread.sleep(500);
        page.locator("#button-account").click();
        // Read the Firstname and Lastname from the Keyword Inputs
        page.locator("#input-payment-firstname").type(input.getString("Firstname"));
        page.locator("#input-payment-lastname").type(input.getString("Lastname"));
        page.locator("#input-payment-email").type("gustav@muster.ch");
        page.locator("#input-payment-telephone").type("+41777777777");
        page.locator("#input-payment-address-1").type("Bahnhofstrasse 1");
        page.locator("#input-payment-city").type("Zurich");
        page.locator("#input-payment-postcode").type("8001");
        page.locator("#input-payment-country").selectOption(new SelectOption().setLabel("Switzerland"));
        page.locator("#input-payment-zone").selectOption(new SelectOption().setLabel("Zürich"));
        page.locator("#button-guest").click();
    }

    @Override
    public void beforeKeyword(String keywordName, Keyword annotation) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();

        Tracing.StartOptions startOptions = new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true);
        context.tracing().start(startOptions);
    }

    @Override
    public boolean onError(Exception e) {
        // Take a screenshot of the page and attach it to the output
        if (page != null) {
            byte[] screenshot = page.screenshot();
            output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(screenshot, "screenshot.png"));
        }
        // Attach the playwright trace to the output
        if (context != null) {
            try {
                Path tempFile = Files.createTempFile("playwright_trace", "");
                try {
                    context.tracing().stop(new Tracing.StopOptions().setPath(tempFile));
                    byte[] bytes = Files.readAllBytes(tempFile);
                    output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(bytes, "trace.zip"));
                } finally {
                    tempFile.toFile().delete();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.onError(e);
    }

    @Override
    public void afterKeyword(String keywordName, Keyword annotation) {
        // Ensure Playwright is properly closed after each keyword execution
        // to avoid process leaks on the agent
        playwright.close();
    }
}
