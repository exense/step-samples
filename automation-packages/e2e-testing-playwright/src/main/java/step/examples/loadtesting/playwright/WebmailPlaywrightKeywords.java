package step.examples.loadtesting.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.text.ParseException;

public class WebmailPlaywrightKeywords extends AbstractKeyword {

    @Keyword(name = "Read order confirmation in Webmail")
    public void readOrderConfirmationInWebmail() throws ParseException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://webmail.exense.ch/");
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            page.locator("#rcmloginuser").fill("opencart-demo-customer1@stepcloud-test.ch");
            page.locator("#rcmloginpwd").fill("fzDwcQy6JcJb5EA");
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