package kl.selenium.medius.opencart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

/*
 * QA Engineer - Pre-technical interview assignment - Selenium
 * Selenium WebDriver test of opencart demo website
 * author: Katarzyna Lebiedzinska
 */
public class OpencartTest {
	WebDriver driver;
	String webpageUrl = "http://demo.opencart.com";
	
	@BeforeMethod
	public void setUp(){
		driver = new FirefoxDriver();
	}
	
	@Test
	public void opencartTest(){
		//1.Open browser and go to http://demo.opencart.com/
		driver.get(webpageUrl);
		
		Assert.assertEquals(driver.getTitle(), "Your Store", "Failed to load store homepage");
		
		
		//2. Change currency to GBP (top left dropdown)
		WebElement currencyDropDownButton = driver.findElement(By.cssSelector(
				"html body.common-home nav#top div.container div.pull-left form#currency div.btn-group button.btn.btn-link.dropdown-toggle"));
		currencyDropDownButton.click();
		WebElement gbpDropDownElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(
				"html body.common-home nav#top div.container div.pull-left form#currency div.btn-group.open ul.dropdown-menu li:nth-of-type(2)")));
		gbpDropDownElement.click();
		
		String actualCurrencySign = driver.findElement(By.cssSelector(
				"html body.common-home nav#top div.container div.pull-left form#currency div.btn-group button.btn.btn-link.dropdown-toggle strong")).getText();
		Assert.assertEquals(actualCurrencySign, "£", "Failed to change currency to GBP");
		
		
		//3. Search for “iPod” using text search
		WebElement searchBox = driver.findElement(By.name("search"));
		searchBox.sendKeys("iPod");
		WebElement searchButton = driver.findElement(By.cssSelector(
				"html body.common-home header div.container div.row div.col-sm-5 div#search.input-group span.input-group-btn button.btn.btn-default.btn-lg"));
		searchButton.click();
		
		Assert.assertEquals(driver.getTitle(), "Search - iPod", "Failed to load search results page");
		
		
		//4. Add all iPods returned in search results to product comparison
		List<WebElement> iPodsInSearchResultsList = driver.findElements(By.className("product-thumb"));
		
		for(WebElement iPod : iPodsInSearchResultsList){
			WebElement addToComparisonButton = iPod.findElement(By.cssSelector("div.button-group button:nth-of-type(3)"));
			addToComparisonButton.click();
			
			WebElement addedToComparisonAlert = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
					"html body.product-search div.container div.alert.alert-success")));
			
			String iPodName = iPod.findElement(By.cssSelector("div.caption h4 a")).getText();
		
			Assert.assertEquals(addedToComparisonAlert.getText(), "Success: You have added " + iPodName + " to your product comparison!\n×",
					"Failed to add product to product comparison");
			
			WebElement closeAlertButton = addedToComparisonAlert.findElement(By.className("close"));
			closeAlertButton.click();
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(
					"html body.product-search div.container div.alert.alert-success")));
		}
		
		
		//5. View product comparison page
		WebElement productComparisonLink = driver.findElement(By.partialLinkText("Product Compare"));
		productComparisonLink.click();
				
		Assert.assertEquals(driver.getTitle(), "Product Comparison", "Failed to load product comparison page");
				
		
		//6. Remove the one that is 'Out Of Stock' from comparison => changed to "Remove all that are 'Out Of Stock" from comparison"
		WebElement productsAvailabilitiesRow = driver.findElement(By.cssSelector(
				"html body.product-compare div.container div.row div#content.col-sm-12 table.table.table-bordered tbody tr:nth-of-type(6)"));
		List<WebElement> availabilitiesList = productsAvailabilitiesRow.findElements(By.tagName("td"));
		
		ArrayList<Integer> outOfStockIndexesList = new ArrayList<Integer>();
		for(WebElement status : availabilitiesList){
			if(status.getText().equals("Out Of Stock")){
				outOfStockIndexesList.add(availabilitiesList.indexOf(status)-1);
			}
		}
		
		List<WebElement> removeFromComparisonButtonsList = driver.findElements(By.linkText("Remove"));
			
		for(int i = outOfStockIndexesList.size()-1; i >= 0 ; i--){
			WebElement removeFromComparisonButton = removeFromComparisonButtonsList.get(outOfStockIndexesList.get(i));
			removeFromComparisonButton.click();
			
			WebElement removedFromComparisonAlert = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
					"html body.product-compare div.container div.alert.alert-success")));
			
			Assert.assertEquals(removedFromComparisonAlert.getText(), "Success: You have modified your product comparison!\n×",
					"Failed to remove product from product comparison");
			
			
			WebElement closeAlertButton = removedFromComparisonAlert.findElement(By.className("close"));
			closeAlertButton.click();
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(
					"html body.product-compare div.container div.alert.alert-success")));
			
			removeFromComparisonButtonsList = driver.findElements(By.linkText("Remove"));
		}
		
		
		productsAvailabilitiesRow = driver.findElement(By.cssSelector(
				"html body.product-compare div.container div.row div#content.col-sm-12 table.table.table-bordered tbody tr:nth-of-type(6)"));
		availabilitiesList = productsAvailabilitiesRow.findElements(By.tagName("td"));
		for(WebElement status : availabilitiesList){
			Assert.assertNotEquals(status.getText(), "Out Of Stock", "Failed to remove out of stock product from product comparison");
		}
		
			
		//7. Add a random available one to shopping cart
		WebElement addToCartButtonsRow = driver.findElement(By.cssSelector(
				"html body.product-compare div.container div.row div#content.col-sm-12 table.table.table-bordered tbody:nth-of-type(2) tr"));
		List<WebElement> addToCartButtonsList = addToCartButtonsRow.findElements(By.tagName("input"));		
		int randomProductIndex = ThreadLocalRandom.current().nextInt(0, addToCartButtonsList.size()-1);		
		addToCartButtonsList.get(randomProductIndex).click();
		
		WebElement addedToCartAlert = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
				"html body.product-compare div.container div.alert.alert-success")));				
		WebElement iPodNamesRow = driver.findElement(By.cssSelector(
				"html body.product-compare div.container div.row div#content.col-sm-12 table.table.table-bordered tbody tr"));
		List<WebElement> iPodNamesList = iPodNamesRow.findElements(By.tagName("td"));
		String addedToCartIPodName = iPodNamesList.get(randomProductIndex+1).getText();
		Assert.assertEquals(addedToCartAlert.getText(), "Success: You have added " + addedToCartIPodName + " to your shopping cart!\n×",
				"Failed to add product to shopping cart");
		
		
		WebElement productPricesRow = driver.findElement(By.cssSelector(
				"html body.product-compare div.container div.row div#content.col-sm-12 table.table.table-bordered tbody tr:nth-of-type(3)"));
		List<WebElement> productPricesList = productPricesRow.findElements(By.tagName("td"));
		String comparisonPagePriceText = productPricesList.get(randomProductIndex+1).getText();
		

		//8. Go to shopping cart and verify that total price matches the one from comparison page for selected product
		WebElement shoppingCartLink = driver.findElement(By.linkText("Shopping Cart"));
		shoppingCartLink.click();
		
		Assert.assertEquals(driver.getTitle(), "Shopping Cart", "Failed to load shopping cart page");
		
		String shoppingCartPriceText = driver.findElement(By.cssSelector(
				"html body.checkout-cart div.container div.row div#content.col-sm-12 div.row div.col-sm-4.col-sm-offset-8 "
				+ "table.table.table-bordered tbody tr:nth-of-type(4) td:nth-of-type(2).text-right")).getText();
		
		Assert.assertEquals(shoppingCartPriceText, comparisonPagePriceText, "Product comparison page price does not match shopping cart total price");		
	}
	
	
	@AfterMethod
	public void tearDown(){
		driver.quit();
	}
}
