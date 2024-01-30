package step.examples.selenium;

import com.codeborne.selenide.Configuration;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class SelenideKeywordExample extends AbstractKeyword {

    @Keyword(name = "Buy MacBook in OpenCart")
    public void buyMacBookInOpenCart() throws InterruptedException {
        // Required to run in Step as chrome isn't in the PATH
        Configuration.browserBinary = "/usr/bin/chromium";
        try {
            open("https://opencart-prf.exense.ch/");
            // ...
        } finally {
            closeWebDriver();
        }
    }
}