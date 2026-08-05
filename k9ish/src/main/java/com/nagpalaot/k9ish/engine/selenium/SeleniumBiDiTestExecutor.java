package com.nagpalaot.k9ish.engine.selenium;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.module.Network;
//import org.openqa.selenium.bidi.network.Cookie;
import org.openqa.selenium.bidi.network.Header;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.nagpalaot.k9ish.engine.BaseTestExecutor;

public class SeleniumBiDiTestExecutor extends BaseTestExecutor {

	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String LINK_TEXT = "linkText";
	public static final String CSS = "css";
	public static final String CLASSNAME = "className";
	public static final String XPATH = "xpath";
	public static final String HEADER = "header";
	public static final String COOKIE = "cookie";
	
	public static final String TARGET_TYPE_TITLE = "TITLE";
	public static final String TARGET_TYPE_URL = "URL";

	protected static final String EQUALS = "=";
	protected static final int DEFAULT_WAIT_TIME_SECONDS = 15;
	
	static protected Network network;
	protected boolean chromeHeadless = false;
	
	//private Headers headers;
	private String testUrl;
	
	//protected Headers getHeaders() {
	//	return headers;
	//}

	//protected void setHeaders(Headers headers) {
	//	this.headers = headers;
	//}

	protected String getTestUrl() {
		return testUrl;
	}

	protected void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public boolean hasDriver() {
		return driver != null;
	}

	public boolean isChromeHeadless() {
		return chromeHeadless;
	}

	public void setChromeHeadless(boolean chromeHeadless) {
		this.chromeHeadless = chromeHeadless;
	}
	
	@Override
	public void initialize() throws Exception {
		ChromeOptions options = new ChromeOptions();
		
		if(chromeHeadless) {
			options.addArguments("--headless=new");
			options.addArguments("--window-size=1920,1080");
		} else {
			options.addArguments("--start-maximized");
		}
		options.enableBiDi();
		System.out.println("Initialize driver");
		driver = new ChromeDriver(options);
		// not using Selenium hub so not using RemoteWebDriver
		//driver = new RemoteWebDriver(new URL("https:://www.bls.gov"), options);
		//driver = new Augmenter().augment(driver);
		network = new Network(driver);
		//network.addIntercept(new AddInterceptParameters(InterceptPhase.RESPONSE_STARTED));
		
		network.onResponseCompleted(responseReceived -> {
			//System.out.println("---------------------");
			//System.out.println(responseReceived.getRequest().getUrl() + " : " + this.getTestUrl());
			//System.out.println("---------------------");
			if(responseReceived.getRequest().getUrl().equalsIgnoreCase(this.getTestUrl())) {
				List<Header> headers = responseReceived.getResponseData().getHeaders();
				Map<String, String> headerMap = getHeaderMap(headers);
				System.out.println(headerMap);
				Set<Cookie> cookies = driver.manage().getCookies();//responseReceived.getRequest().getCookies();
				//List<String> cookies = getCookies(headers);
				System.out.println(cookies);
				printCookies(cookies);
			}
			
		});
		
        setSuccessful(true);
	}
	
	private void printCookies(Set<Cookie> cookies) {
		for(Cookie cookie : cookies) {
			System.out.println("Cookie name: " + cookie.getName());
			System.out.println("Value: " + cookie.getValue());
			System.out.println("Domain: " + cookie.getDomain());
			System.out.println("Path: " + cookie.getPath());
			System.out.println("Samesite: " + cookie.getSameSite());
			System.out.println("Expiration: " + cookie.getExpiry());
			System.out.println("HTTPOnly: " + cookie.isHttpOnly());
			System.out.println("Secure: " + cookie.isSecure());
			System.out.println("Cookie: " + cookie.toString() + "\n") ;
		}
	}
	
	protected List<String> getCookies(List<Header> headerList){
		List<String> result = new ArrayList<String>();
		for(Header header : headerList) {
			if(header.getName().equalsIgnoreCase("Set-Cookie")) {
				result.add(header.getValue().getValue());
			}
		}
		return result;
	}
	
	protected Map<String, String> getHeaderMap(List<Header> headerList){
		Map<String, String> result = new HashMap<String, String>();
		for(Header header : headerList) {
			result.put(header.getName(), header.getValue().getValue());
		}
		return result;
	}

	@Override
	public void shutdown() {
		if(hasDriver()) {
			driver.quit();
		}
	}

	@Override
	protected void handleOpenCommand(String url) {
		System.out.println("Open URL " + url);
		this.setTestUrl(url);
		driver.get(url);
	}

	@Override
	protected void handleAssertPresentCommand(String target, String expectedValue) {
		/*
		 * repurposing the method to also be able to process looking for a header, 
		 * maybe a cookie later, along with processing looking for a regular element.
		 */
		if(target.startsWith(HEADER)) {
			System.out.println("Find a header check");
			String headerTarget = target.split(EQUALS)[1];
			handleAssertPresentHeaderCommand(headerTarget, expectedValue);
		}
		else {
			System.out.println("Find a regular element check");
			WebElement element = driver.findElement(locatorForTarget(target));
			if(!commonAssertContain(element, target, expectedValue)) {
				setSuccessful(false);
			}
		}
	}
	
	/**
	 * Better solution is to add a new command using this method to specifically 
	 * check for headers.  It requires a listener which puts the headers info 
	 * into the headers variable.
	 * This also requires use of Selenium DevTools which works best with Chromium 
	 * based browsers (and associated webdrivers) but should also work with RemoteWebDriver.
	 */
	@Override
	protected void handleAssertPresentHeaderCommand(String target, String expectedValue) {
		/*System.out.println("Check headers " + headers);
		if(headers == null || !headers.containsKey(target)){
			System.out.println("Header not found.");
			setSuccessful(false);
		}
		else {
			String actualValue = (String) headers.get(target);
			System.out.println("Compare expected value: " + expectedValue + " with actual value: " + actualValue);
			if(!expectedValue.equalsIgnoreCase(actualValue.toLowerCase())) {
				System.out.println("Expected value not found");
				setSuccessful(false);
			}
		}*/
	}

	@Override
	protected void handleWaitThenAssertPresentCommand(String target, String expectedValue) {
		WebElement element = findElementWithWait(target, DEFAULT_WAIT_TIME_SECONDS);
		if(!commonAssertContain(element, target, expectedValue)) {
			setSuccessful(false);
		}
	}

	protected By locatorForTarget(String target) {
		if(target.startsWith(NAME)) {
			return By.name(target.split(EQUALS)[1]);
		} else if(target.startsWith(CSS)) {
			return By.cssSelector(target.split(EQUALS)[1]);
		} else if(target.startsWith(ID)) {
			return By.id(target.split(EQUALS)[1]);
		} else if (target.startsWith(LINK_TEXT)) {
			return By.linkText(target.split(EQUALS)[1]);
		}
		return null;
	}
	
	protected WebElement findElementWithWait(String target, int timeOutInSec) {
		WebElement element = null;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSec));
		for(int retry = 0; retry < 2; retry++) {
			try {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated(locatorForTarget(target)));
				break;
			} catch (Exception ex) {
				if(retry == 1) {
					throw new NoSuchElementException(target);
				}
			}
		}
		return element;
	}
	
	private boolean commonAssertContain(WebElement targetElement, String targetLocation, String expectedText) {
		boolean textFoundAtTarget = true;
		if(expectedText == null || expectedText.trim().isEmpty()) {
			return false;
		}
		String actualValue = targetElement.getText();
		if(actualValue != null && !actualValue.contains(expectedText)) {
			textFoundAtTarget = false;
		}
		return textFoundAtTarget;
	}
}
