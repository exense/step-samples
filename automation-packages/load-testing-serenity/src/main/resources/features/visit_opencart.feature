Feature: Visit Opencart

  Scenario: Search for an article in Opencart
    Given I navigate to the Opencart homepage
    When I search for an article with the keyword "Mac"
    And I click on the first item in the search results
    Then I should be redirected to the article's detail page