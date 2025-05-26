package step.examples.serenity.pages;


import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.Dimension;

public class OpencartHomePage extends PageObject {

    @FindBy(xpath = "//*[@id='search']/input")
    WebElementFacade searchInput;

    @FindBy(xpath = "//*[@id='search']/span/button/i")
    WebElementFacade searchButton;

    @FindBy(xpath = "//div[@class='product-thumb']//a")
    WebElementFacade firstArticle;

    @Step
    public void launchApplication() {
        open();
        getDriver().manage().window().setSize(new Dimension(1920, 1080));
    }

    @Step
    public void searchArticle(String searchKey) {
        searchInput.sendKeys(searchKey);
        searchButton.click();
    }

    @Step
    public void openFirstItem() {
        firstArticle.click();
    }
}
