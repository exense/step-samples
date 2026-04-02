package step.examples.loadtesting.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Input;
import step.handlers.javahandler.Keyword;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenCartPlaywrightKeywords extends AbstractKeyword {

    @Keyword(name = "Purchase product in OpenCart")
    public void purchaseProductInOpenCart(@Input(name = "Product", defaultValue = "MacBook") String product) throws InterruptedException, IOException {
        try (Playwright playwright = Playwright.create()){
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true));

            Page page = context.newPage();
            page.navigate("https://opencart-prf.stepcloud.ch/");
            page.locator("text=" + product).click();
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
            page.locator("#button-shipping-method").click();
            page.locator("//input[@name='agree']").setChecked(true);
            page.locator("#button-payment-method").click();
            page.locator("#button-confirm").click();
            page.locator("text=Your order has been placed!").isVisible();

            Path path = Paths.get("trace.zip");
            context.tracing().stop(new Tracing.StopOptions().setPath(path));
            // Attachment uploaded as classical attachment
            output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(Files.readAllBytes(path), "Playwright Trace", "application/vnd.step.playwright-trace+zip"));
        }
    }
}