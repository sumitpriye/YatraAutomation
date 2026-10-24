package com.yatra.automation;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript {

	public static void main(String[] args) throws InterruptedException {


		ChromeOptions chromeoptions = new ChromeOptions();
		chromeoptions.addArguments("--start-maximized");
		chromeoptions.addArguments("--disable-notifications");

		WebDriver driver = new ChromeDriver(chromeoptions);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Synchronizing WebDriver
		driver.get("https://www.yatra.com/");
		driver.manage().window().maximize();

		By departureDateButtonLocator = By.xpath("//div[@aria-label='Departure Date inputbox' and @role='button']");

		WebElement departureDatebutton = wait
				.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator)); // Synchronized //default
																								// polling time -500ms
		departureDatebutton.click();

		WebElement currentMonthElement = getMonthCalendarWebElement(wait, 0);
		WebElement nextMonthElement = getMonthCalendarWebElement(wait, 1);

		Thread.sleep(2000);
		Map<String, Object> lowestPriceInCurrentMonth = getLowestPriceInAMonth(currentMonthElement);
		Map<String, Object> lowestPriceInNextMonth = getLowestPriceInAMonth(nextMonthElement);
		int lowestPriceCurrentMonth = (int) lowestPriceInCurrentMonth.get("lowestPrice");
		int lowestPriceNextMonth = (int) lowestPriceInNextMonth.get("lowestPrice");

		for (Map.Entry<String, Object> set : lowestPriceInCurrentMonth.entrySet()) {

			System.out.println("Current Month " + set.getKey() + " is " + set.getValue());

		}
		System.out.println("*******************************************************************************");
		for (Map.Entry<String, Object> set : lowestPriceInNextMonth.entrySet()) {

			System.out.println("Next Month " + set.getKey() + " is " + set.getValue());

		}
		System.out.println("*******************************************************************************");

		comparePrice(lowestPriceCurrentMonth, lowestPriceNextMonth);

	}

	public static WebElement getMonthCalendarWebElement(WebDriverWait wait, int index) {

		By calendarMonthsLocator = By.xpath("//div[@class='react-datepicker__month-container']");
		List<WebElement> calendarMonthsList = wait
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calendarMonthsLocator));
		return calendarMonthsList.get(index);
	}

	public static Map<String, Object> getLowestPriceInAMonth(WebElement monthElement) {
		By priceLocator = By.xpath(".//span[contains(@class,'custom-day-content')]");
		List<WebElement> pricesElement = monthElement.findElements(priceLocator);
		int minPrice = Integer.MAX_VALUE;
		WebElement priceDateElement = null;
		for (WebElement price : pricesElement) {

			String priceValue = price.getText();
			if (priceValue.length() > 0) {
				int intPrice = Integer.parseInt(priceValue.replace("₹", "").replace(",", ""));

				if (intPrice <= minPrice) {
					minPrice = intPrice;
					priceDateElement = price.findElement(By.xpath(".//..//.."));
				}
			}

		}

		String minPriceDate = priceDateElement.getAttribute("aria-label").split(", ")[1];
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lowestPrice", minPrice);
		map.put("lowestPriceDate", minPriceDate);
		return map;
	}

	public static void comparePrice(int lowestCurrentMonthPrice, int lowestNextMonthPrice) {
		if (lowestCurrentMonthPrice < lowestNextMonthPrice) {
			System.out.println("Current Month price is lowest :" + lowestCurrentMonthPrice);
		} else if (lowestCurrentMonthPrice == lowestNextMonthPrice) {
			System.out.println("Both Month prices are same :" + lowestCurrentMonthPrice);
		} else {
			System.out.println("Next Month price is lowest :" + lowestNextMonthPrice);
		}

	}

}
