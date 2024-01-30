package step.examples.selenium;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import static com.codeborne.selenide.Selenide.open;

public class SelenideKeywordExample extends AbstractKeyword {

    @Keyword(name = "Buy MacBook in OpenCart")
    public void buyMacBookInOpenCart() throws InterruptedException {
        open("https://opencart-prf.exense.ch/");
    }
}