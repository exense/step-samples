package step.examples.loadtesting.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class WebmailPlaywrightKeywords extends AbstractKeyword {

    @Keyword(name = "Read order confirmation in Webmail")
    public void readOrderConfirmationInWebmail() {
        try (PlaywrightWrapper playwrightWrapper = PlaywrightWrapper.create()){
            Browser browser = playwrightWrapper.playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://demo-webmail.exense.ch/");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.locator("#rcmloginuser").fill("customer@opencart.demo");
            page.locator("#rcmloginpwd").fill("8Fm#%GzdSocv3o");
            page.locator("#rcmloginsubmit").click();

            page.locator("#messagelist").isVisible();
            page.locator("#rcmcountdisplay").isVisible();

            // Click the first email sent by "Your Store"
            page.locator("text=Your Store").nth(0).click();

            FrameLocator iframe = page.frameLocator("#messagecontframe");
            String title = iframe.locator("//h2[@class='subject']").innerText();
            String date = iframe.locator("//div[@class='header-summary']//span[@class='text-nowrap']").innerText();
            //Date dateParsed = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);

            output.add("EmailTitle", title);
            output.add("EmailDate", date);
        }
    }
}