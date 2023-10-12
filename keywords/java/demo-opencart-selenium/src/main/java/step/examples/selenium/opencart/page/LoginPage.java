package step.examples.selenium.opencart.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import step.examples.selenium.opencart.object.PageObject;

@PageObject
public class LoginPage {

	private final WebDriver driver;

	private WebElement email;

	private WebElement password;

	@FindBy(css = "button[type='submit']")
	private WebElement submitButton;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}

	public LoginPage setEmail(String text) {
		email.clear();
		email.sendKeys(text);
		return this;
	}

	public LoginPage setPassword(String text) {
		password.clear();
		password.sendKeys(text);
		return this;
	}

	public IndexPage clickSubmit() {
		submitButton.click();
		return new IndexPage(driver);
	}

}