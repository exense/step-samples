package step.examples.serenity.pages;


import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

public class OpencartArticlePage extends PageObject {

    @FindBy(id = "tab-description")
    WebElementFacade descriptionTab;

    @Step
    public String getProductDescription() {
        String value = descriptionTab.getValue();
        return value;
    }
}
