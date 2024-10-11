package step.examples.serenity.stepdefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import step.examples.serenity.pages.OpencartArticlePage;
import step.examples.serenity.pages.OpencartHomePage;

public class OpencartStepDefinition {
    @Steps
    OpencartHomePage opencartPage;

    @Steps
    OpencartArticlePage opencartArticlePage;

    @Given("I navigate to the Opencart homepage")
    public void i_navigate_to_the_opencart_homepage() {
        // Navigate to Opencart homepage
        opencartPage.launchApplication();
    }

    @When("I search for an article with the keyword {string}")
    public void i_search_for_an_article_with_the_keyword(String keyword) {
        // Find the search bar and enter the search keyword
        opencartPage.searchArticle(keyword);
    }

    @When("I click on the first item in the search results")
    public void i_click_on_the_first_item_in_the_search_results() {
        // Click on the first search result
        opencartPage.openFirstItem();
    }

    @Then("I should be redirected to the article's detail page")
    public void i_should_be_redirected_to_the_article_detail_page() {
        opencartArticlePage.getProductDescription();
    }

}
