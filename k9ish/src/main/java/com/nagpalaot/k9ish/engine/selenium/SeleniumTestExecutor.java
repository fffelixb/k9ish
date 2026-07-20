package com.nagpalaot.k9ish.engine.selenium;

import java.time.Duration;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v150.network.Network;
import org.openqa.selenium.devtools.v150.network.model.Headers;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.nagpalaot.k9ish.engine.BaseTestExecutor;

public class SeleniumTestExecutor extends BaseTestExecutor {

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

	protected  static final String EQUALS = "=";
	protected static final int DEFAULT_WAIT_TIME_SECONDS = 15;
	
	protected boolean chromeHeadless = false;
	
	private Headers headers;
	private String testUrl;
	
	protected Headers getHeaders() {
		return headers;
	}

	protected void setHeaders(Headers headers) {
		this.headers = headers;
	}

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
		options.setBrowserVersion("149");
		if(chromeHeadless) {
			options.addArguments("--headless=new");
			options.addArguments("--window-size=1920,1080");
		} else {
			options.addArguments("--start-maximized");
		}
		System.out.println("Initialize driver");
		driver = new ChromeDriver(options);
		// not using Selenium hub so not using RemoteWebDriver
		//driver = new RemoteWebDriver(new URL("https:://www.bls.gov"), options);
		//driver = new Augmenter().augment(driver);
		//DevTools devTools = ((HasDevTools) driver).getDevTools();
		DevTools devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		// Listen for response received events
        devTools.addListener(Network.responseReceived(), response -> {
        	if(getTestUrl()!= null && !getTestUrl().isBlank()) {
        		if(response.getResponse().getUrl().equalsIgnoreCase(getTestUrl())) {
        			System.out.println("Pass headers for testUrl " + getTestUrl());
        			setHeaders(response.getResponse().getHeaders());
        		}
        	}
        });
        setSuccessful(true);
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
		WebElement element = driver.findElement(locatorForTarget(target));
		if(!commonAssertContain(element, target, expectedValue)) {
			setSuccessful(false);
		}
	}
	
	@Override
	protected void handleAssertPresentHeaderCommand(String target, String expectedValue) {
		System.out.println("Check headers " + headers);
		if(headers == null || !headers.containsKey(target)){
			setSuccessful(false);
		}
		else {
			String actualValue = (String) headers.get(target);
			if(!expectedValue.equalsIgnoreCase(actualValue)) {
				setSuccessful(false);
			}
		}
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
