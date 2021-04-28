package step.examples.selenium.opencart.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import step.examples.selenium.opencart.object.PageObject;

@PageObject
public class IndexPage {

	@FindBy(how = How.LINK_TEXT, using = "Desktops")
	private WebElement desktops;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Laptops")
	private WebElement laptops;

	@FindBy(how = How.LINK_TEXT, using = "Components")
	private WebElement components;

	@FindBy(how = How.LINK_TEXT, using = "Tablets")
	private WebElement tablets;

	private WebDriver driver;

	public IndexPage(WebDriver driver) {this.driver = driver;}

	public IndexPage gotoDesktopsCategory() {
		clickSubMenuLink(desktops, "PC");
		clickSubMenuLink(desktops, "Mac");
		return this;
	}

	public IndexPage gotoLaptopsCategory() {
		clickSubMenuLink(laptops, "Macs");
		clickSubMenuLink(laptops, "Windows");
		return this;
	}

	public IndexPage gotoComponentsCategory() {
		clickSubMenuLink(components, "Mice and Trackballs");
		clickSubMenuLink(components, "Monitors");
		clickSubMenuLink(components, "Printers");
		clickSubMenuLink(components, "Scanners");
		clickSubMenuLink(components, "Web Cameras");
		return this;
	}

	public IndexPage gotoTabletsCategory() {
		tablets.click();
		return this;
	}

	private void hoverHoverLink(WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).perform();
	}

	private void clickSubMenuLink(WebElement parentElement, String partialLinkText) {
		hoverHoverLink(parentElement);
		driver.findElement(By.partialLinkText(partialLinkText)).click();
	}

}